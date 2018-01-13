/*
 *  Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.wso2.org;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

public class EmailAttachmentSender {

    public static void sendEmailWithAttachments(String host, String port,
                                                final String userName, final String password, String toAddress,
                                                String subject, String message)
            throws  MessagingException {
        // sets SMTP server properties
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.user", userName);
        properties.put("mail.password", password);
        //properties.put("mail.smtp.ssl.trust", "tygra.wso2.com");


        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, password);
            }
        };
        Session session = Session.getInstance(properties, auth);

        // creates a new e-mail message
        Message msg = new MimeMessage(session);

        msg.setFrom(new InternetAddress(userName));

        for (int i = 0; i < toAddress.length(); i++) {
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(toAddress));
        }




        msg.setSubject(subject);
        msg.setSentDate(new Date());

        // creates message part
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setContent(message, "text/html");

        // creates multi-part
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(messageBodyPart);


        // sets the multi-part as e-mail's content
        msg.setContent(multipart);
        // sends the e-mail
         Transport.send(msg);
        System.out.println("Email sent.");

        // System.out.println(message);

    }

    /**
     * Test sending e-mail with attachments
     */
    public static void main(String args[]) throws IOException, SQLException {

        ReadConfigureFile credentials= new ReadConfigureFile();
        // SMTP info
        String host = credentials.getsmtpHost();
        String port = credentials.getsmtpPort();
        String mailFrom = credentials.getmailFrom();
        String mailPassword = credentials.getmailPassword();
        String url = credentials.getDatabaseConn();
        Connection conn = DriverManager.getConnection(url, credentials.getUser(), credentials.getPassword());
        Statement create = conn.createStatement();

        GenerateDate printDate=new GenerateDate();
        String currentDate=printDate.GetCurrentDate();



        //String message = "https://identity-gateway.cloud.wso2.com/t/wso2internal928/gitopenprdashboard/";

        String openPRSummary = "SELECT distinct Product,count(PullUrl) as countPR FROM RetrieveOpenPR where OpenWeeks>1 group by Product having countPR order by countPR desc";




        StringBuilder message1 = new StringBuilder();

        //PreparedStatement create = conn.prepareStatement(openPRSummary);
        message1.append("<html><head><style>    table {\n" + "        font-family: arial, sans-serif;\n" + "        border-collapse: collapse;\n" + "        width: 60%;\n" + "    }\n" + "\n" + "     th {\n" + "        border: 2px solid black;\n" + "        padding: 8px;\n" + "        font-size: 16px;\n" + "    }\n" + "    td{\n" + "            border: 2px solid black;\n" + "            padding: 8px;\n" + "            font-size: 14px;\n" + "    }\n" + "\n" + "    tr:nth-child(even) {\n" + "        background-color: #dddddd;\n" + "    }</style></head><body><center>"
                + "<table style='border:2px solid black'>");

        message1.append("<th bgcolor=\"#1ab2ff\"><b>Product<b></th>");
        message1.append("<th bgcolor=\"#1ab2ff\"><b>Number of open PR more than a week</b></th>");

        ResultSet results = create.executeQuery(openPRSummary);

        ArrayList<String> productName = new ArrayList<String>();

        while (results.next()) {

            String product=results.getString("Product");
            productName.add(product);

            message1.append("<tr>");
            message1.append("<td><b>");
            message1.append(product);
            message1.append("</b></td>");

            message1.append("<td><center><b>");
            message1.append(results.getString("countPR"));
            message1.append("</b></center></td>");


            message1.append("<tr>");
        }

        message1.append("</table></center><br><br><br><br></body></html>");


        ArrayList<String> mailTo = new ArrayList<String>();

        String groupEmail = "";
        String subject1="";
        String subject="";


        for (int y = 1; y < 4; y++) {
            groupEmail = credentials.getGroupEmail(y);
            System.out.println(groupEmail);
            mailTo.add(groupEmail + "@yahoo.com");

            subject1 = "open PRs-" + groupEmail;

            if (subject1.contains("_group") && !subject1.contains("engineering-group")) {
                subject = subject1.replaceAll("_group", "")+" - "+currentDate;
                System.out.println(subject);




                for (String productList : productName) {
                    System.out.println(productList);

                    if (productList.equals("yadhury")) {
                        //String groupName = (String) hashMap.get("yadhury");
                        //  subject = "open PRs-" + groupName;

                        ResultSet SummaryOpenPRYadhu;
                        StringBuilder messageNew = new StringBuilder();

                        SummaryOpenPRYadhu = create.executeQuery("select * from RetrieveOpenPR where Product='yadhury' and OpenWeeks>1 order by OpenWeeks desc");

                        int x = messageAppend(SummaryOpenPRYadhu, messageNew);

                        try {

                            System.out.println("xxxxxxxxxxxxxxxxxx" + x);
                            if (x == 1) {
                                messageNew.append(message1);

                                sendEmailWithAttachments(host, port, mailFrom, mailPassword, mailTo.get(y - 1), subject, messageNew.toString());
                            }

                        } catch (Exception ex) {
                            System.out.println("Could not send email.");
                            ex.printStackTrace();
                        }
                    }
                    if (productList.equals("senthan")) {
                        // String groupName = (String) hashMap.get("senthan");
                        System.out.println("aoirhgkljrng.kewf.,jaebgkjagf      " + groupEmail);

                        // subject = "open PRs-" + groupName;
                        ResultSet SummaryOpenPR;
                        StringBuilder messageNew = new StringBuilder();

                        messageNew.append(message1);
                        SummaryOpenPR = create.executeQuery("select * from RetrieveOpenPR where Product='senthan' and OpenWeeks>1 order by OpenWeeks desc");
                        int openPRS = messageAppend(SummaryOpenPR, messageNew);

                        try {
                            if (openPRS == 1) {
                                sendEmailWithAttachments(host, port, mailFrom, mailPassword, mailTo.get(y - 1), subject, messageNew.toString());
                            }

                        } catch (Exception ex) {
                            System.out.println("Could not send email.");
                            ex.printStackTrace();
                        }
                    }


                    //System.out.println(SummaryOpenPR);


                }
            } else {
                subject = "All open PRs - " + currentDate;
                System.out.println(subject);

                ResultSet SummaryOpenPR;

                SummaryOpenPR = create.executeQuery("select * from RetrieveOpenPR where OpenWeeks>1 order by OpenWeeks desc");
                message1.append("<html><head><style>    table {\n" + "        font-family: arial, sans-serif;\n" + "        border-collapse: collapse;\n" + "        width:100%;\n" + "    }\n" + "\n" + "     th {\n" + "        border: 2px solid black;\n" + "        padding: 8px;\n" + "        font-size: 16px;\n" + "    }\n" + "    td{\n" + "            border: 2px solid black;\n" + "            padding: 8px;\n" + "            font-size: 14px;\n" + "    }\n" + "\n" + "    tr:nth-child(even) {\n" + "        background-color: #dddddd;\n" + "    }</style></head><body>" + "<table style='border:2px solid black'>");


                message1.append("<th bgcolor=\"bfff80\"'><b>Product</b></th>");
                message1.append("<th bgcolor=\"bfff80\"'><b>RepoUrl</b></th>");
                message1.append("<th bgcolor=\"bfff80\"'><b>GitId</b></th>");
                message1.append("<th bgcolor=\"bfff80\"'><b>PullUrl</b></th>");
                message1.append("<th bgcolor=\"bfff80\"'><b>OpenHours</b></th>");
                message1.append("<th bgcolor=\"bfff80\"'><b>OpenDays</b></th>");
                message1.append("<th bgcolor=\"bfff80\"'><b>OpenWeeks</b></th>");
                message1.append("<th bgcolor=\"bfff80\"'><b>OpenMonths</b></th>");
                message1.append("<th bgcolor=\"bfff80\"'><b>OpenYears</b></th>");

                while (SummaryOpenPR.next()) {

                    message1.append("<tr>");
                    message1.append("<td><b>");
                    message1.append(SummaryOpenPR.getString("Product"));
                    message1.append("</b></td>");


                    message1.append("<td><b>");
                    message1.append(SummaryOpenPR.getString("RepoUrl"));
                    message1.append("</b></td>");


                    message1.append("<td><b>");
                    message1.append(SummaryOpenPR.getString("GitId"));
                    message1.append("</b></td>");


                    message1.append("<td><b>");
                    message1.append(SummaryOpenPR.getString("PullUrl"));
                    message1.append("</b></td>");

                    message1.append("<td><center><b>");
                    message1.append(SummaryOpenPR.getInt("OpenHours"));
                    message1.append("</b></center></td>");

                    message1.append("<td><center><b>");
                    message1.append(SummaryOpenPR.getString("OpenDays"));
                    message1.append("</b></center></td>");

                    message1.append("<td><center><b>");
                    message1.append(SummaryOpenPR.getString("OpenWeeks"));
                    message1.append("</b></center></td>");

                    message1.append("<td><center><b>");
                    message1.append(SummaryOpenPR.getString("OpenMonths"));
                    message1.append("</b></center></td>");

                    message1.append("<td><center><b>");
                    message1.append(SummaryOpenPR.getInt("OpenYears"));
                    message1.append("</b></center></td>");

                    message1.append("<tr>");


                }
                try {



                    sendEmailWithAttachments(host, port, mailFrom, mailPassword, mailTo.get(y - 1),
                            subject, message1.toString());
                    //System.out.println(message.toString());
                    //System.out.println(mailTo.get(y-1));
                } catch (Exception ex) {
                    System.out.println("Could not send email.");
                    ex.printStackTrace();
                }
            }
        }




    }

    private static int messageAppend(ResultSet summaryOpenPR, StringBuilder messageNew) throws SQLException {
        int noOfOpenPrs = 0;
        messageNew.append("<html><head><style>    table {\n" + "        font-family: arial, sans-serif;\n" + "        border-collapse: collapse;\n" + "        width:100%;\n" + "    }\n" + "\n" + "     th {\n" + "        border: 2px solid black;\n" + "        padding: 8px;\n" + "        font-size: 16px;\n" + "    }\n" + "    td{\n" + "            border: 2px solid black;\n" + "            padding: 8px;\n" + "            font-size: 14px;\n" + "    }\n" + "\n" + "    tr:nth-child(even) {\n" + "        background-color: #dddddd;\n" + "    }</style></head><body>"
                + "<table style='border:2px solid black'>");



        messageNew.append("<th bgcolor=\"bfff80\"'><b>Product</b></th>");
        messageNew.append("<th bgcolor=\"bfff80\"'><b>RepoUrl</b></th>");
        messageNew.append("<th bgcolor=\"bfff80\"'><b>GitId</b></th>");
        messageNew.append("<th bgcolor=\"bfff80\"'><b>PullUrl</b></th>");
        messageNew.append("<th bgcolor=\"bfff80\"'><b>OpenHours</b></th>");
        messageNew.append("<th bgcolor=\"bfff80\"'><b>OpenDays</b></th>");
        messageNew.append("<th bgcolor=\"bfff80\"'><b>OpenWeeks</b></th>");
        messageNew.append("<th bgcolor=\"bfff80\"'><b>OpenMonths</b></th>");
        messageNew.append("<th bgcolor=\"bfff80\"'><b>OpenYears</b></th>");


        while (summaryOpenPR.next()) {
            messageNew.append("<tr>");
            messageNew.append("<td><b>");
            messageNew.append(summaryOpenPR.getString("Product"));
            //System.out.println(SummaryOpenPR.getString("Product"));
            messageNew.append("</b></td>");


            messageNew.append("<td><b>");
            messageNew.append(summaryOpenPR.getString("RepoUrl"));
            messageNew.append("</b></td>");


            messageNew.append("<td><b>");
            messageNew.append(summaryOpenPR.getString("GitId"));
            messageNew.append("</b></td>");


            messageNew.append("<td><b>");
            messageNew.append(summaryOpenPR.getString("PullUrl"));
            messageNew.append("</b></td>");

            messageNew.append("<td><center><b>");
            messageNew.append(summaryOpenPR.getInt("OpenHours"));
            messageNew.append("</b></center></td>");

            messageNew.append("<td><center><b>");
            messageNew.append(summaryOpenPR.getString("OpenDays"));
            messageNew.append("</b></center></td>");

            messageNew.append("<td><center><b>");
            messageNew.append(summaryOpenPR.getString("OpenWeeks"));
            messageNew.append("</b></center></td>");

            messageNew.append("<td><center><b>");
            messageNew.append(summaryOpenPR.getString("OpenMonths"));
            messageNew.append("</b></center></td>");

            messageNew.append("<td><center><b>");
            messageNew.append(summaryOpenPR.getInt("OpenYears"));
            messageNew.append("</b></center></td>");

            messageNew.append("<tr>");

            noOfOpenPrs++;
            //System.out.println(noOfOpenPrs);


        }
        return noOfOpenPrs;

    }
}


