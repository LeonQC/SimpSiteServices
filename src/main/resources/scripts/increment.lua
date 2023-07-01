local nextId = 0
nextId = redis.call('INCR', KEYS[1])
return nextId


