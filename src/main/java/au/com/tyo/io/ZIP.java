/*
 * Copyright (C) 2015 TYONLINE TECHNOLOGY PTY. LTD. (TYO Lab)
 * 
 */

package au.com.tyo.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import au.com.tyo.io.FileUtils.Progress;

public class ZIP implements IOConstants {
	
	public static void unpackZip(String zipFile, String outPath, Progress progress, long totalFileCount) throws IOException { 
	    InputStream is;
        is = new FileInputStream(zipFile);
        unzip(is, outPath, progress, totalFileCount);
        is.close();
	}

	public static void unzip(InputStream is, String outPath, Progress progress, long totalFileCount) throws IOException {
	     ZipInputStream zis;
         String filename;

         zis = new ZipInputStream(new BufferedInputStream(is));          
         ZipEntry ze;

         byte[] buffer = new byte[BUFFER_SIZE];
         int bytesRead = 0;
         int count = 0;
         double percent = 0.0;
         
         while ((ze = zis.getNextEntry()) != null) 
         {
             // zapis do souboru
             filename = ze.getName();
             
             // Need to create directories if not exists, or
             // it will generate an Exception...
             if (ze.isDirectory()) {
                File fmd = new File(outPath + filename);
                fmd.mkdirs();
                continue;
             }
             
             FileOutputStream fout = new FileOutputStream(outPath + filename);

             // cteni zipu a zapis
             while ((bytesRead = zis.read(buffer)) != -1) 
             {
                 fout.write(buffer, 0, bytesRead);             
             }

             fout.close();
             
            if (progress != null) {
            	++count;
            	if (totalFileCount > 0)
            		percent = (double)count / (double) totalFileCount;
            	else
            		percent += 0.00001;
            	progress.infoProgress(Double.valueOf((percent  * 100)).intValue());
            }
             zis.closeEntry();
         }

         zis.close();
	}
	
	public static void extractOne(File file, String outPath, String filename) throws IOException {
		ZipFile zipFile = new ZipFile(file);
		ZipEntry entry = zipFile.getEntry(filename);
		InputStream is = zipFile.getInputStream(entry);
		
        FileOutputStream fout = new FileOutputStream(outPath + filename);

        byte[] buffer = new byte[BUFFER_SIZE];
        int bytesRead = 0;
        // cteni zipu a zapis
        while ((bytesRead = is.read(buffer)) != -1) 
        {
            fout.write(buffer, 0, bytesRead);             
        }

        fout.close(); 
        is.close();
        zipFile.close();
	}
}
