package com.hellofresh.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.testng.IInvokedMethod;
import org.testng.IResultMap;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.reporters.SuiteHTMLReporter;
import org.testng.xml.XmlSuite;

import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

public class ExtentReporterNG extends SuiteHTMLReporter {
    private ExtentReports extent;
    
    
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        extent = new ExtentReports(outputDirectory + File.separator + "Hello Fresh API TEST Automation"+"_ExtentReport.html", true);
	    extent.loadConfig(new File(System.getProperty("user.dir") + "/src/main/resources/extent-config.xml"));
        
        for (ISuite suite : suites) {
            Map<String, ISuiteResult> result = suite.getResults();
           
  
            for (ISuiteResult r : result.values()) {
                ITestContext context = r.getTestContext();
                try {
                	
	                buildTestNodes(context.getPassedTests(), LogStatus.PASS);
	                buildTestNodes(context.getFailedTests(), LogStatus.FAIL);
	                buildTestNodes(context.getSkippedTests(), LogStatus.SKIP);
                } catch (IOException ioe) {
                	throw new RuntimeException(ioe);
                }
            }
        }
  
        extent.flush();
        extent.close();
        
    }
                
        @SuppressWarnings("unchecked")
		public void generateMethodsChronologically(List<XmlSuite> xmlSuite, ISuite suite,
        	      String outputFileName){
        	 Collection<IInvokedMethod> invokedMethods = suite.getAllInvokedMethods();
             Comparator<? super ITestNGMethod>  alphabeticalComparator = new Comparator<Object>(){
           	  @Override
           	  public int compare(Object o1, Object o2) {
           	    IInvokedMethod m1 = (IInvokedMethod) o1;
           	    IInvokedMethod m2 = (IInvokedMethod) o2;
           	    return m1.getTestMethod().getMethodName().compareTo(m2.getTestMethod().getMethodName());
           	         	
        }};
     	Collections.sort((List) invokedMethods, alphabeticalComparator);
        	 
             }
  
    private void buildTestNodes(IResultMap tests, LogStatus status) throws FileNotFoundException, IOException {
        ExtentTest test = null;
        
        if (tests.size() > 0) {
            for (ITestResult result : tests.getAllResults()) {
            	if (result.getParameters() == null || result.getParameters().length ==0) {
            		test = extent.startTest(result.getMethod().getMethodName());
            	} else {
                    for(int i = 0; i < result.getParameters().length; i++){
                    	if (result.getParameters()[i] != null) {
                    		test = extent.startTest(result.getParameters()[i].toString());
                            break;
                    	}
                    }
                    
            	}
            	
                test.getTest().setStartedTime(getTime(result.getStartMillis()));
                test.getTest().setEndedTime(getTime(result.getEndMillis()));
  
                for (String group : result.getMethod().getGroups()) {
                    test.assignCategory(group);
                }
  
                String message = "Test " + status.toString().toLowerCase() + "ed";
  
                if (result.getThrowable() != null) {
                    message = result.getThrowable().getMessage();
                }
                
                test.log(status, message);
                if (getFilePath(result) != null) {
                	File file = new File(getFilePath(result));
                	if (file.exists()) {
                		String body = IOUtils.toString(new FileInputStream(file));
                		test.log(LogStatus.INFO, body);
                	}
                }
                for(int i = 0; i < result.getParameters().length; i++){
                	if (result.getParameters()[i] != null) {
                        test.log(LogStatus.INFO, result.getParameters()[i].toString());
                	}
                }
                extent.endTest(test);
            }
        }
    }
    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();        
    }
       
	private String getFilePath(ITestResult testResult) {
		String path = System.getProperty("user.dir") + File.separator + "test-output" + File.separator + "images";
		File filePath = new File(path);
		filePath.mkdirs();

		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date(testResult.getEndMillis()));
		String filePathName = filePath.getAbsolutePath()  + File.separator +  testResult.getName() + timeStamp + ".txt";
		return filePathName;
	}

}
