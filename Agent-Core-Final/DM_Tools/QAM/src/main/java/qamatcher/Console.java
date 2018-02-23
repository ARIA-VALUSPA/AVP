package qamatcher;

/**
 * class Console
 * <br> A thread safe class providing static synchronised  methods
 * <br> to read/write numbers and strings
 * <br> and arrays from/to standard input
 * @author R. op den Akker
 * @version oct. 2000
 */

public class Console {

  /**
   * print a prompt on the console
   * one extra space is printed after the string
   * but no extra newline is printed
   * @param prompt the prompt string to display
   */
  public static synchronized void printPrompt(String prompt)
  {  System.out.print(prompt + "\n");
    System.out.flush();
  }

  /**
   * write a String on standard output
   * no extra space is printed
   * no extra newline is printed
   */
  public static synchronized void writeString(String prompt)
  {  System.out.print(prompt);
    System.out.flush();
  }

  /**
   * read a string from the console. The string is
   * terminated by a newline
   * @return the input string (without the newline)
   */
  public static synchronized String readString()
  {  int ch;
    String r = "";
    boolean done = false;
    while (!done)
    {  try
    {  ch = System.in.read();

      if (ch < 0 || (char)ch == '\n' || (char)ch == '\r')
      { done = true; System.in.skip(1); }
      else
        r = r + (char) ch;
    }
    catch(java.io.IOException e)
    {  done = true;
    }
    }
    return r;
  }

  /**
   * read a string from the console. The string is
   * terminated by a newline
   * @param prompt the prompt string to display
   * @return the input string (without the newline)
   */
  public static synchronized String readString(String prompt)
  {  //printPrompt(prompt);
    writeString(prompt);
    return readString();
  }


  /**
   * read an integer from the console. The input is
   * terminated by a newline
   * @param prompt the prompt string to display
   * @return the input value as an int
   * @exception NumberFormatException if bad input
   */
  public static synchronized int readInt(String prompt){
    empty();
    while(true)
    {  printPrompt(prompt);
      try
      {  return Integer.parseInt(readString().trim());
      } catch(NumberFormatException e)
      {  System.out.println
              ("Not an integer. Please try again!");
      }
    }
  }

  // empty the InputStream
  private static synchronized void empty(){
    try{
      int n = System.in.available();
      int c;
      for (int i=0 ; i< n; i++)
        c = System.in.read();
    }
    catch(Exception exc){}
  }

  /**
   * read a floating point number from the console.
   * The input is terminated by a newline
   * @param prompt the prompt string to display
   * @return the input value as a double
   * @exception NumberFormatException if bad input
   */
  public static synchronized double readDouble(String prompt) {
    empty();
    while(true)
    {  printPrompt(prompt);
      try
      {  return Double.valueOf
              (readString().trim()).doubleValue();
      } catch(NumberFormatException e)
      {  System.out.println
              ("Not a floating point number. Please try again!");
      }
    }
  }


  /**
   *  read a given number of integer from standard input
   *  into an array
   *  @param n  number of integers prompted for
   *  @return array of integers
   */
  public static synchronized int [] readIntArray(int n) {
    int r [] = new int [n];
    for ( int i = 0 ; i < n ; i++)
      r[i] = readInt("type an integer : ");
    return r;
  }

  /**
   *  read a given number of double from standard input
   *  into an array
   *  @param n  number of double prompted for
   *  @return array of double
   */
  public static synchronized double [] readDoubleArray(int n) {
    double r [] = new double [n];
    for ( int i = 0 ; i < n ; i++)
      r[i] = readDouble("type a double : ");
    return r;
  }


  /**
   *  show array of integers on standard output
   *  @param a[]  array of integers
   */
  public static synchronized void showArray(int a []) {
    for ( int i = 0 ; i < a.length ; i = i + 1 )
      System.out.print(a[i] + " ");
  } // showArray

  /**
   *  show array of Integer objects on standard output
   *  @param  a[] array of Integer
   */
  public static synchronized void showArray(Integer a []) {
    for ( int i = 0 ; i < a.length ; i = i + 1 )
      System.out.print(a[i] + " ");
  } // showArray

  /**
   *  show array of String objects on standard output
   *  @param  a[] array of  String
   */
  public static synchronized void showArray(String a []) {
    for ( int i = 0 ; i < a.length ; i = i + 1 )
      System.out.print(a[i] + " ");
  } // showArray

} // Console


