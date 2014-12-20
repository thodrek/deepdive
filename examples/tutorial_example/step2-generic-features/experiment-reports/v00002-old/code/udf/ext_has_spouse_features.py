#! /usr/bin/env python

import sys
import ddlib     # DeepDive python utility

ARR_DELIM = '~^~'

# For each input tuple
for row in sys.stdin:
  parts = row.strip().split('\t')
  
  # Get all fields from a row
  words = parts[0].split(ARR_DELIM)
  lemmas = parts[1].split(ARR_DELIM)
  poses = parts[2].split(ARR_DELIM)
  dependencies = parts[3].split(ARR_DELIM)
  ners = parts[4].split(ARR_DELIM)
  relation_id = parts[5]
  p1_start, p1_length, p2_start, p2_length = [int(x) for x in parts[6:]]

  # Get a sentence from ddlib -- array of "Word" objects
  sentence = ddlib.get_sentence(
    [0, ] * len(words),  [0, ] * len(words), words, lemmas, poses,
    dependencies, ners)

  # Unpack input into tuples.
  span1 = ddlib.Span(begin_word_id=p1_start, length=p1_length)
  span2 = ddlib.Span(begin_word_id=p2_start, length=p2_length)

  # Features for this pair come in here
  features = set()
  for feature in ddlib.get_generic_features_relation(sentence, span1, span2):
    features.add(feature)
  for feature in features:
    print str(relation_id) + '\t' + feature