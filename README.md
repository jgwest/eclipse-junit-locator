# Eclipse Update Site JUnit Test Locator

This utility allows the user to identify and analyze JUnit test cases and test suites inside a given Eclipse update site/JAR. Large Eclipse-based products will often contain thousands of JUnit tests, which can make wrangling them all quite difficult. This utility may be used to programatically analyze and reason against those test classes, using a Java or JSON API.

First, generate a JSON representation of the JUnit test case/suite classes inside a zipped Eclipse update site:
```
git clone https://github.com/jgwest/eclipse-junit-locator
cd eclipse-junit-locator/JUnitLocator
mvn package
cd target
java -jar JUnitLocator.jar "path to update site zip" "path to output json file"
```

To parse the above generated JSON file and load it into a test database object:
```
TestClassDatabase db = new TestClassDatabase();

try {
	db.initialize(ConsumerUtils.readResults(new FileInputStream("d:\\tests.json")));

	// Print information for a specific test class
	Optional<Object> o = db.getTestClassById("com.your.plugin.name",  "com.you.TestClass");
	JUnitTestCase tc = (JUnitTestCase)o.get();
	System.out.println(tc.getPathInZip());
	
	// Print all parents and children
	for(JUnitTestSuite ts : tc.getParents()) {

		System.out.println(ts);

		for(Object child : ts.getChildren()) {
			System.out.println("- "+child);
		}

	}

} catch (FileNotFoundException e) {
	e.printStackTrace();
}

```
