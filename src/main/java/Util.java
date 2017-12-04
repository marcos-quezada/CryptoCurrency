/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;

/**
 *
 * @author arrojo
 */
public class Util {
    public static String InvocaServlet(URL url) throws Exception {
        URLConnection con = url.openConnection();

        if (url.toString().toUpperCase().contains("HTTPS")) {

            Authenticator au = new Authenticator() {

                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("usuario", "clave".toCharArray());
                }
            };
            Authenticator.setDefault(au);
        }

        StringBuilder sBuf = new StringBuilder();
        BufferedReader bReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String line = null;
        while ((line = bReader.readLine()) != null) {
            sBuf.append(line);
        }
        return sBuf.toString();
    }
}
