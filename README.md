# Persistent Queue
Persistent queue for java.

## Overview
It's built upon a simple queue(it does not implement the java queue interface).  
It's backed by a memory mapped file, with the options to use a read cache.

It will reuse the QueueFile so you'll have consistent size.
So the queue is bound by size. Not elements but data.

## Structure

### File
* READ_POSITION: int
* WRITE_POSITION: int

### Data
* DATA_LENGTH: int
* DATA: byte[]

## Configuration
* Location
* QueueFileSize
