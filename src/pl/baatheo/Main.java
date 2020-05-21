package pl.baatheo;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {

        Stegano stegano = new Stegano("golab.jpg", "Test szyfrowania stegano bartosz porebski", 8);
        stegano.encrypt();
        stegano.decrypt();



    }
}
