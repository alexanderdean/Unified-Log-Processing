package plum;

import net.sargue.mailgun.*;

import plum.avro.Alert;

public final class Emailer {

  static final String MAILGUN_KEY =
    "b5ca946169cf5e9e3da913adb5164587-059e099e-b8a64376";                                    // a
  static final String MAILGUN_SANDBOX =
    "sandbox64e16a38b40342f4a81a723f09f2ee85.mailgun.org";                                   // b

  private static final Configuration configuration = new Configuration()
    .domain(MAILGUN_SANDBOX)
    .apiKey(MAILGUN_KEY)
    .from("Test account", "postmaster@" + MAILGUN_SANDBOX);

  public static void send(Alert alert) {                                
    Response resp = Mail.using(configuration)
      .to(alert.recipient.email.toString())
      .subject(alert.notification.summary.toString())
      .text(alert.notification.detail.toString())
      .build()
      .send();
    System.out.println(resp.responseMessage());
  }
}
