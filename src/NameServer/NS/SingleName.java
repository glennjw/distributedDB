package NameServer.NS;

public class SingleName {
    Integer key;
    String value;

    SingleName (String txt) {
        this.key = Integer.parseInt( txt.trim().split(" ")[0] );
        this.value = txt.trim().split(" ")[1];
    }

    SingleName (String key, String value) {
        this.key = Integer.parseInt(key);
        this.value = value;
    }

    SingleName (Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    SingleName() {

    }
}
