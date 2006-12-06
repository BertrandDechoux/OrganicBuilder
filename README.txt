//*************************************************************************************
//*******************************  Compilation Notes    *******************************	  
//*************************************************************************************

  I compile using the command:
		
	javac -target 1.3 -source 1.3 -bootclasspath c:\jre\1.3.1_13\lib\rt.jar -extdirs "" OrganicBuilder.java
		  
  or similar because then the applet is compatible with older JVMs. But you don't need to do this for local use, just use 

	javac OrganicBuilder.java

  To make the class files into a jar, I use:

	jar cmf manifest.mf OrganicBuilder.jar *.class *.gif
			

