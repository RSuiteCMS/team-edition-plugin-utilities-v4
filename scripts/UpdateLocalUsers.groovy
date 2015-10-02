//Updates local users from an RSuite users.data file that is piped in, like:
// run40 groovy/UpdateLocalUsers.groovy < groovy/users.fruit.dat
// -----------------------------------------------------------------------

import com.reallysi.rsuite.client.api.*


rsuite.login();


try {
  System.in.eachLine { line ->
    if (line.trim().equals("") ||
        line.startsWith("#") ||
        line.startsWith("version:")) {
      return;
    }

    def a = line.split(':');
    def uid = a[0];
    if (uid.startsWith("-")) {
      uid = uid.substring(1);
      def user = rsuite.getLocalUser(uid);
      if (user != null) {
        println "Removing "+uid+"...";
        rsuite.removeLocalUser(uid);
      } else {
        println "Remove: User \""+uid+"\" does not exist, skipping";
      }

    } else {
      def pass = a[1];
      def name = a[2];
      def mail = a[3];
      def roles = a[4].replace(' ',',');

      def user = rsuite.getLocalUser(uid);
      if (user == null) {
        if (pass.equals("") || pass.equals("*") || pass.equals("RESET")) {
          pass = randomPassword();
        }
        println "Adding "+uid+" (password="+pass+")";
        rsuite.createLocalUser(uid, pass, name, mail, roles);
      } else {
        println "Updating "+uid;
        rsuite.updateLocalUser(uid, name, mail, roles);
        if (pass != "" && pass != "*") {
          if (pass.equals("RESET")) {
            pass = randomPassword();
            println "Setting "+uid+"'s password to "+pass;
          } else {
            println "Updating "+uid+"'s password";
          }
          rsuite.setLocalUserPassword(uid, pass);
        }
      }
    }
  };

} finally {
  rsuite.logout();
}

//===========================================================================

def randomPassword() {
  def rnd = new Random();
  def validChars =
    "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890abcdefghijklmnopqrstuvwxyz";
  def maxIndex = validChars.length()
  return (1..10).sum {
    validChars[ rnd.nextInt(maxIndex) ]
  };
}
