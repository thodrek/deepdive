package org.deepdive.settings

import org.deepdive.Logging
import scala.util.parsing.combinator.RegexParsers
import scala.language.postfixOps

object FactorFunctionParser extends RegexParsers with Logging {
  def relationOrField = """[\w]+""".r
  def arrayDefinition = """\[\]""".r
  def equalPredicate = """[0-9]+""".r

  def implyFactorFunction = ("Imply" | "IMPLY") ~> "(" ~> rep1sep(factorVariable, ",") <~ ")" ^^ { varList =>
    ImplyFactorFunction(varList)
  }

  def orFactorFunction = ("Or" | "OR") ~> "(" ~> rep1sep(factorVariable, ",") <~ ")" ^^ { varList =>
    OrFactorFunction(varList)
  }

  def xorFactorFunction = ("Xor" | "XOR") ~> "(" ~> rep1sep(factorVariable, ",") <~ ")" ^^ { varList =>
    XorFactorFunction(varList)
  }

  def andFactorFunction = ("And" | "AND") ~> "(" ~> rep1sep(factorVariable, ",") <~ ")" ^^ { varList =>
    AndFactorFunction(varList)
  }

  def equalFactorFunction = ("Equal" | "EQUAL") ~> "(" ~> factorVariable ~ ("," ~> factorVariable) <~ ")" ^^ {
    case v1 ~ v2 =>
    EqualFactorFunction(List(v1, v2))
  }

  def isTrueFactorFunction = ("IsTrue" | "ISTRUE") ~> "(" ~> factorVariable <~ ")" ^^ { variable =>
    IsTrueFactorFunction(List(variable))
  }

  def multinomialFactorFunction = ("Multinomial" | "MULTINOMIAL") ~> "(" ~> rep1sep(factorVariable, ",") <~ ")" ^^ { varList =>
    MultinomialFactorFunction(varList)
  }

  def linearFactorFunction = ("Linear" | "LINEAR") ~> "(" ~> rep1sep(factorVariable, ",") <~ ")" ^^ { varList =>
    LinearFactorFunction(varList)
  }

  def ratioFactorFunction = ("Ratio" | "RATIO") ~> "(" ~> rep1sep(factorVariable, ",") <~ ")" ^^ { varList =>
    RatioFactorFunction(varList)
  }

  def logicalFactorFunction = ("Logical" | "LOGICAL") ~> "(" ~> rep1sep(factorVariable, ",") <~ ")" ^^ { varList =>
    LogicalFactorFunction(varList)
  }

  def oneIsTrueFactorFunction = ("OneIsTrue" | "ONEISTRUE") ~> "(" ~> rep1sep(factorVariable, ",") <~ ")" ^^ { varList =>
    OneIsTrueFactorFunction(varList)
  }

  def factorVariable = ("!"?) ~ rep1sep(relationOrField, ".") ~ (arrayDefinition?) ~
    (("=" ~> equalPredicate)?) ^^ {
    case (isNegated ~ varList ~ isArray ~ predicate)  =>
      FactorFunctionVariable(varList.take(varList.size - 1).mkString("."), varList.last,
        isArray.isDefined, isNegated.isDefined, readLong(predicate))
  }

  def readLong(predicate: Option[String]) : Option[Long] = {
    predicate match {
      case Some(number) => Some(number.toLong)
      case None => None
    }
  }

  def factorFunc = implyFactorFunction | orFactorFunction | andFactorFunction |
    equalFactorFunction | isTrueFactorFunction | xorFactorFunction | multinomialFactorFunction |
    linearFactorFunction | ratioFactorFunction | logicalFactorFunction | oneIsTrueFactorFunction

}
