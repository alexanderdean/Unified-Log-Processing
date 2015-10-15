doSomethingInteresting();
log.info("Did something interesting");
doSomethingLessInteresting();
log.debug("Did something less interesting");

// Log output: #a
// INFO  2014-03-14 10:50:14,125 [Log4jExample_main]  "org.alexanderdean.Log4jExample": Did something interesting
// INFO  2014-03-14 10:55:34,345 [Log4jExample_main]  "org.alexanderdean.Log4jExample": Did something less interesting

