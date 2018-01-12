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


import java.io.*;
import java.util.Properties;

public class ReadConfigureFile {
    protected Properties prop = null;
    protected InputStream input = getClass().getResourceAsStream("/config.properties");
    //BufferedReader reader = new BufferedReader(new InputStreamReader(input));

    public ReadConfigureFile() throws IOException {

        prop = new Properties();
        prop.load(input);
    }


    public String getGroupEmail(int i) {
        return prop.getProperty("group" + i);
    }
    public String getDatabaseConn()
    {
        return prop.getProperty("databaseUrl");
    }
    public String getUser()
    {
        return prop.getProperty("user");
    }
    public String getPassword()
    {
        return prop.getProperty("password");
    }
    public String getsmtpHost()
    {
        return prop.getProperty("host");
    }
    public String getsmtpPort()
    {
        return prop.getProperty("port");
    }
    public String getmailFrom(){

        return prop.getProperty("mailFrom");
    }
    public String getmailPassword(){

        return prop.getProperty("mailPassword");
    }



}
