///*
// * Copyright 2007 The JA-SIG Collaborative. All rights reserved. See license
// * distributed with this file and available online at
// * http://www.ja-sig.org/products/cas/overview/license/index.html
// */
//package com.shdy.admin;
//
//import lombok.extern.slf4j.Slf4j;
//
//import javax.net.ssl.HostnameVerifier;
//import javax.net.ssl.HttpsURLConnection;
//import javax.net.ssl.SSLSession;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//
///**
// * Abstract class that knows the protocol for validating a CAS ticket.
// *
// * @author Scott Battaglia
// * @version $Revision$ $Date$
// * @since 3.1
// */
//@Slf4j
//public abstract class AbstractCasProtocolUrlBasedTicketValidator extends AbstractUrlBasedTicketValidator {
//
//    protected AbstractCasProtocolUrlBasedTicketValidator(
//            final String casServerUrlPrefix) {
//        super(casServerUrlPrefix);
//    }
//
//    HostnameVerifier hv = new HostnameVerifier() {
//        public boolean verify(String urlHostName, SSLSession session) {
//            System.out.println("Warning: URL Host: " + urlHostName + " vs. "
//                    + session.getPeerHost());
//            return true;
//        }
//    };
//
//    /**
//     * Retrieves the response from the server by opening a connection and merely
//     * reading the response.
//     */
//    protected final String retrieveResponseFromServer(final URL validationUrl,
//                                                      final String ticket) {
//        HttpURLConnection connection = null;
//
//        //System.out.println("============================================================");
//
//        try {
//            trustAllHttpsCertificates();
//            HttpsURLConnection.setDefaultHostnameVerifier(hv);
//
//            connection = (HttpURLConnection) validationUrl.openConnection();
//            final BufferedReader in = new BufferedReader(new InputStreamReader(
//                    connection.getInputStream()));
//
//            String line;
//            final StringBuffer stringBuffer = new StringBuffer(255);
//
//            synchronized (stringBuffer) {
//                while ((line = in.readLine()) != null) {
//                    stringBuffer.append(line);
//                    stringBuffer.append("\n");
//                }
//                return stringBuffer.toString();
//            }
//
//        } catch (final IOException e) {
//            log.error(e.getMessage());
//            return null;
//        } catch (final Exception e1) {
//            log.error(e1.getMessage());
//            return null;
//        } finally {
//            if (connection != null) {
//                connection.disconnect();
//            }
//        }
//    }
//
//    private static void trustAllHttpsCertificates() throws Exception {
//        javax.net.ssl.TrustManager[] trustAllCerts = new javax.net.ssl.TrustManager[1];
//        javax.net.ssl.TrustManager tm = new miTM();
//        trustAllCerts[0] = tm;
//        javax.net.ssl.SSLContext sc = javax.net.ssl.SSLContext
//                .getInstance("SSL");
//        sc.init(null, trustAllCerts, null);
//        javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sc
//                .getSocketFactory());
//    }
//
//    static class miTM implements javax.net.ssl.TrustManager,
//            javax.net.ssl.X509TrustManager {
//        public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//            return null;
//        }
//
//        public boolean isServerTrusted(
//                java.security.cert.X509Certificate[] certs) {
//            return true;
//        }
//
//        public boolean isClientTrusted(
//                java.security.cert.X509Certificate[] certs) {
//            return true;
//        }
//
//        public void checkServerTrusted(
//                java.security.cert.X509Certificate[] certs, String authType)
//                throws java.security.cert.CertificateException {
//            return;
//        }
//
//        public void checkClientTrusted(
//                java.security.cert.X509Certificate[] certs, String authType)
//                throws java.security.cert.CertificateException {
//            return;
//        }
//    }
//
//}
