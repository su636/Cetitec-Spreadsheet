package spreadsheet;

public class dataCell {
	public boolean hasData = false;
	public boolean equation = false;
	public boolean processing = false;
	public boolean err = false;
	

	public String data="";
	
	
	public dataCell(String rawData) {
		if(rawData.length()!=0) {
			hasData=true;
			if(rawData.charAt(0)=='=') {
				equation =true;
			}else if(rawData.charAt(0)=='#') {
				err=true;
			}
			data=rawData;
		}
		
	}
}
