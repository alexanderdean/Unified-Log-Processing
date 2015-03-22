#!/usr/bin/env python

import json, time
from threading import Thread
from boto import kinesis
from boto.kinesis.exceptions import ProvisionedThroughputExceededException

class ShardReader(Thread):
  def __init__(self, name, stream_name, shard_id):
    super(ShardReader, self).__init__(None, name)
    self.name = name
    self.stream_name = stream_name
    self.shard_id = shard_id

  @staticmethod
  def detect_incident(event):
    decoded = json.loads(event)
    passed = None, None
    try:
      server = decoded['on']['server']['hostname']
      metrics = decoded['direct_object']['filesystem_metrics']
      pct_avail = metrics['available'] * 100 / metrics['size']
      return (server, pct_avail) if pct_avail <= 20 else passed
    except KeyError:
      return passed

  def run(self):
    try:
      next_iterator = conn.get_shard_iterator(self.stream_name,
        self.shard_id, 'TRIM_HORIZON')['ShardIterator']
      while True:
        response = conn.get_records(next_iterator, limit=10)
        for event in response['Records']:
          print '{} read event {}'.format(self.name, event['PartitionKey'])
          s, a = self.detect_incident(event['Data'])              # a
          if a:
            print '{} has only {}% disk available!'.format(s, a)  # b
        next_iterator = response['NextShardIterator']
        time.sleep(5)
    except ProvisionedThroughputExceededException as ptee:
       print 'Caught: {}'.format(ptee.message)
       time.sleep(5)

if __name__ == '__main__':
  conn = kinesis.connect_to_region(region_name="us-east-1",
    profile_name="ulp")
  stream = conn.describe_stream('events')
  shards = stream['StreamDescription']['Shards']

  threads = []                                                    # c
  for shard in shards:
    shard_id = shard['ShardId']
    reader_name = 'Reader-{}'.format(shard_id)
    reader = ShardReader(reader_name, 'events', shard_id)
    reader.start()
    threads.append(reader)                                        # c

  for thread in threads:                                          # d
    thread.join()
