package plum;

import net.sargue.mailgun.*;

import plum.avro.Alert;

public final class Emailer {

  static final String MAILGUN_KEY =
    "key-bd3c7d427836872bf02eb80b4b6a62fe";                                                  // a
  static final String MAILGUN_SANDBOX =
    "sandbox26321840e2114f06be28daad732d9795.mailgun.org";                                   // b

  private static final Configuration configuration = new Configuration()
    .domain(MAILGUN_SANDBOX)
    .apiKey(MAILGUN_KEY)
    .from("Test account", "postmaster@" + MAILGUN_SANDBOX);

  public static void send(Alert alert) {                                
    MailBuilder.using(configuration)
      .to(alert.recipient.email.toString())
      .subject(alert.notification.summary.toString())
      .text(alert.notification.detail.toString())
      .build()
      .send();
  }
}
