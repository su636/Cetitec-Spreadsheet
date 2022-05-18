/*Created on May/9/2022
 * Created by Peter Su
 * This file contain a class that take input files formulated as spreadsheet and process it to print outputs into a text file. The class can only handle int data type and simple + - * / operations.
 */

package spreadsheet;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;


public class MainClass {
	
	
	
	//main method
 public static void main(String[] args) throws IOException 
	 {
	 Scanner reader = new Scanner(System.in); 
	 System.out.println("Please pleace input files in the testFiles folder and enter the file name: ");
	 String fn = reader.next();
	
	 
	 String fileName = "testFiles/"+fn;
	 String fileOutput = "outputFiles/"+fn+"Output.txt";
	 
	 int[] size = countRowsAndColumns(fileName);
	 
	 dataCell[][] master = readFile(fileName, size[0], size[1]);
	 
	 processDataSet(master, size[0], size[1]);
	 
	 
	 writeOutput( fileOutput, size[0], size[1], master);
	 
		 
	 }
 
 
 
 /*This method is used to determine the row and column number of the 2D array. 
 It will return an array with the first element as number of rows and second 
element as number of columns*/
 public static int[] countRowsAndColumns(String filename) throws IOException {
	 System.out.println("Analyzing input file......");
	 int[] result = new int[2];
	 int rows = 0;
	 int columns = 1;
	 
	 File file = new File(filename);
	 Scanner sc =new Scanner(file);
	 while(sc.hasNextLine()){
		 String line=sc.nextLine();
		 char[] lineArray=line.toCharArray();
		 if(lineArray.length>0) {
			 int currentColumns=1;
			 for(int i=0; i<lineArray.length; i++) {
				 
				 if(lineArray[i]=='\t') {
					 currentColumns++;
				 }				 
			 }
			 if(currentColumns>columns) {
				 columns=currentColumns;
			 }
		 }
		 rows++;
	 }
	 sc.close();
	 
	 result[0]=rows;
	 result[1]=columns;
	 return result;
 }
 
 
 
//This method is used to read data file into a 2D array.
 public static dataCell[][] readFile(String filename, int rows, int columns) throws IOException{
	 System.out.println("Reading input file into 2D array......");
	 dataCell[][] master = new dataCell[rows][columns];
	 
	 File file = new File(filename);
	 Scanner sc =new Scanner(file);
	 while(sc.hasNextLine()){
		for(int i=0; i<rows; i++) {
			String rowData = sc.nextLine().toString();
//			System.out.println("Read data line: " + rowData);
//			System.out.println("String data length:" + rowData.length());
			int divider = 0;
			for(int j=0; j<columns; j++) {
				if(divider<=rowData.length()) {
					int oldDivider=divider;
//					System.out.println("divider before checking"+divider);
					divider=rowData.indexOf("\t", divider);
//					System.out.println("divider after checking"+divider);
					if(divider==-1){
//						System.out.println("Got a -1"+divider);
						String info = rowData.substring(oldDivider);
//						System.out.println("Info for now"+info);
						dataCell justRead = new dataCell(info);
//						System.out.println("get info into row: "+i+", column: " + j);
						master[i][j]=justRead;
						divider=rowData.length()+1;
					}else if(divider>oldDivider) {
						String info = rowData.substring(oldDivider, divider);
//						System.out.println("didn't get -1, now info is: "+info);
						dataCell justRead = new dataCell(info);
						master[i][j]=justRead;
						divider++;
					}else if(divider==oldDivider){
						String info = "";
//						System.out.println("same divider, put zero length string into master. "+info);
						dataCell justRead = new dataCell(info);
						master[i][j]=justRead;
						divider++;
					}
					
				}
			}
		}
	 }
	 sc.close();
	 
	 
 return master;

}



//This method is used to process master data set.
 public static void processDataSet(dataCell[][] dataSet, int row, int col) {
     System.out.println("Data processing started......");
	 for(int i=0; i<row; i++) {
		 for(int j=0; j<col; j++) {
			 if(dataSet[i][j]!=null) {
				 if(dataSet[i][j].equation) {
					 dataSet[i][j] =processDataCell(dataSet, row, col, i, j);
					 
				 } 
			 }
		 }
	 }
	 System.out.println("Data processing Finished!");
	 
 }



//This method is used to process a data cell 
public static dataCell processDataCell(dataCell[][] dataSet, int row, int col, int targetRow, int targetCol) {
	System.out.println("Processing cell ["+ targetRow + " " + targetCol + " ]......");
	
	dataCell result= new dataCell("");
	if(dataSet[targetRow][targetCol].hasData==true&&dataSet[targetRow][targetCol].equation==false) {
		result=dataSet[targetRow][targetCol];
	}else if(dataSet[targetRow][targetCol].hasData==true||dataSet[targetRow][targetCol].equation==true||dataSet[targetRow][targetCol].err==false) {
		
		dataSet[targetRow][targetCol].processing=true;
		String data = dataSet[targetRow][targetCol].data.substring(1);
	
		String[] cellsUnprocess = data.split("\\+|\\-|\\*|\\/");
		
		String[] cellsProcessed=new String[cellsUnprocess.length];
		
		for(int i=0; i<cellsUnprocess.length; i++) {			
			cellsProcessed[i]=getData(dataSet, row, col, cellsUnprocess[i]);			
		}

		
		
		
		for(int i=0; i<cellsProcessed.length; i++) {
			if(cellsProcessed[i].equals("#ERROR")) {
				System.out.println("Detected infine loop!");
				result = new dataCell(cellsProcessed[i]);
				result.err=true;
				result.data=cellsProcessed[i];
			}
		}
		if(result.err==false) {
			for(int i=0; i<cellsProcessed.length; i++) {
				if(cellsProcessed[i].equals("#NAN")) {
					System.out.println("Detected missing data!");
					result = new dataCell(cellsProcessed[i]);
					result.err=true;
					result.data=cellsProcessed[i];
					result.hasData=true;
				}
			}
		}
		if(result.err==false) {
			
			for(int i=0; i<cellsProcessed.length; i++) {
				if(cellsProcessed[i].charAt(0)=='=') {
					System.out.println("About to get in recursion!");					System.out.println(cellsProcessed[i]);
					dataCell temp=new dataCell("");
					temp = processDataCell(dataSet, row, col, (Integer.parseInt(cellsUnprocess[i].substring(1))-1),(cellsUnprocess[i].charAt(0) - 'A'));
					
					if(temp.err==true) {
						result.data=temp.data;
						result.hasData=true;
						result.err=true;
					}else {
						cellsProcessed[i]=temp.data;
					}
				}
			}
		}
		if(result.err==false) {
			
				
				String data2=data.replaceAll("[0-9]", "");
				String data3=data2.replaceAll("[A-Z]", "");
				
				String[] operators = new String[data3.length()];
			
				for(int i=0; i<data3.length(); i++) {
					operators[i]=data3.substring(i, i+1);
				}
				result.data=calculate (operators, cellsProcessed);
				result.hasData=true;
			}
		dataSet[targetRow][targetCol].processing=false;
		}
	System.out.println("Cell ["+ targetRow + " " + targetCol + " ] processed.");
	 System.out.println("Result is hasData:"+ result.hasData +" | Equation: "+result.equation +" | Error: "+ result.err +" | Data:"+ result.data + ".");
	return result;
	
}



//This method is used to locate data cell from coordinate and extract data from data cell
public static String getData(dataCell[][] dataSet, int row, int col, String coor) {
	System.out.println("Geting data from "+ coor);
	String data="";
	if(Character.isDigit(coor.charAt(0))) {
		data=coor;
	}else {
		dataCell target=new dataCell("");
		int colNum=coor.charAt(0) - 'A';
		int rowNum=Integer.parseInt(coor.substring(1))-1;
		if(rowNum>(row-1)||colNum>(col-1)) {
			data="#NAN";
		}else {
			target=dataSet[rowNum][colNum];
			if(target==null||target.hasData==false) {
				data="#NAN";
			}else if(target.processing==true) {
				data="#ERROR";
			}else if(target.hasData==true&&target.processing==false) {
				data=target.data;
			}
		}
	}
	
	
	
	return data;
}



//This method is used to do the calculation after confirm that all operands contains regular data.
public static String calculate(String[] operators, String[] operands){
	System.out.println("Evaluating equation......");
	
	int answer=Integer.parseInt(operands[0]);	
	
	
	for(int i=0; i<operators.length; i++ ) {
		
		if(operators[i].equals("*") ){
		
			int temp=Integer.parseInt(operands[i])*Integer.parseInt(operands[i+1]);
			operands[i]="0";
			operands[i+1]=String.valueOf(temp);
			if(i>0) {
				operators[i]=operators[i-1];
			}else if(i==0) {
				operators[i]="+";
				answer=0;
			}
			
		}
		else if(operators[i].equals("/")) {
			
			int temp=Integer.parseInt(operands[i])/Integer.parseInt(operands[i+1]);
			operands[i]="0";
			operands[i+1]=String.valueOf(temp);
			if(i>0) {
				operators[i]=operators[i-1];
			}else if(i==0) {
				operators[i]="+";
				answer=0;
			}
		}
	}

	
	
	for(int i=0; i<operators.length; i++ ) {
		if(operators[i].equals("+")) {
		
			answer+=Integer.parseInt(operands[i+1]);
			
		}
		else if(operators[i].equals("-")) {
			answer-=Integer.parseInt(operands[i+1]);
		}
	}
	
	
	
	return String.valueOf(answer);
}



//This method is used to write output files.
public static void writeOutput(String filename, int row, int col, dataCell[][] master) throws IOException {
	File output = new File(filename);
	FileWriter writer = new FileWriter(output);
	PrintWriter pen = new PrintWriter(writer);
    
	System.out.println("Writing data into output file: " + filename+ "......");     
	      for(int i=0; i<row; i++) {
	    	  for(int j=0; j<col; j++) {
	    		  if(master[i][j]!=null) {
	    			  if(master[i][j].hasData==true) {
	    				  
	    				  pen.write(master[i][j].data);
	    			  }
	    			  pen.write('\t');
	    		  }
	    	  }
	    	  pen.write('\n');
	      }
	      pen.close();
	      
	    
	      System.out.println("Output file: " + filename+ " is ready to view! "); 
	  }

}