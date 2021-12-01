package Target;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MetData
{
	private Date startDate,endDate = null;
	
	private Common common = new Common();
	private ArrayList<ArrayList<Object>> metDataArrays = new ArrayList<ArrayList<Object>>();
	
	private ld_mod ldModClass = new ld_mod();
	
	public final static int datetime=0;
	public final static int Ta=1;
	public final static int RH=2;
	public final static int WS=3;
	public final static int P=4;
	public final static int Kd=5;
	public final static int Ld=6;
	

	
	public MetData(String file, String mod_ldown)
	{
		super();
		readFile(file, mod_ldown);
		System.out.println(mod_ldown);
	}
	public MetData(String file, String mod_ldown, Date date1A, Date date2)
	{
		super();
		this.startDate=date1A;
		this.endDate=date2;
		readFile(file, mod_ldown);
	}
	
	public ArrayList<ArrayList<Object>> getMetData()
	{
		return metDataArrays;
	}

	public void readFile(String file, String mod_ldown)
	{
//		datetime,Ta,RH,WS,P,Kd,Ld
//		05/02/2009 0:00, 21.9, 57,  5,1008.0,0.0,386.77
				
		int count = 0;
		ArrayList<String> cfmFile = common.readTextFileToArray(file);
		for (String line : cfmFile)
		{
			if (count == 0)
			{
				count ++;
				continue;
			}
			line = line.trim();
			String[] splitLine = line.split(",");
			
			if (mod_ldown.equals("N") && splitLine.length < 7)
			{
				System.out.println(line);
				System.out.println("Not enough columns in Met data. Required fields: datetime,Ta,RH,WS,P,Kd,Ld");
				System.exit(1);
			}
			else if (splitLine.length < 6)
			{
				System.out.println(line);
				System.out.println("Not enough columns in Met data. Required fields: datetime,Ta,RH,WS,P,Kd");
				System.exit(1);
			}
			
			ArrayList<Object> dataLine = new ArrayList<Object>();
			
			
			String datetimeStr = splitLine[datetime];
			String[] datetimeStrSplit = datetimeStr.split(" ");
			String dateStr = datetimeStrSplit[0];
			String[] dateStrSplit = dateStr.split("/");
			String day = dateStrSplit[0];
			String month = dateStrSplit[1];
			String year = dateStrSplit[2];
			Integer dayInt = new Integer(day).intValue();
			Integer monthInt = new Integer(month).intValue()-1;
			Integer yearInt = new Integer(year).intValue();
			String timeStr = datetimeStrSplit[1];
			String[] timeStrSplit = timeStr.split(":");
			String hour = timeStrSplit[0];
			String minute = timeStrSplit[1];
			Integer hourInt = new Integer(hour).intValue();
			Integer minuteInt = new Integer(minute).intValue();
			
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR,yearInt);
			cal.set(Calendar.MONTH,monthInt);
			cal.set(Calendar.DAY_OF_MONTH,dayInt);		 
			cal.set(Calendar.HOUR_OF_DAY,hourInt);
			
			cal.set(Calendar.MINUTE,minuteInt);
			cal.set(Calendar.SECOND,0);
			cal.set(Calendar.MILLISECOND,0);

			Date d = cal.getTime();	
			
			
			if (startDate != null)
			{

				if ( d.getTime() <  startDate.getTime() )
				{
					continue;
				}
				if ( d.getTime() >  endDate.getTime() )
				{
					continue;
				}
	
			}
			
//			System.out.println(line);
			
			
			double ws = new Double(splitLine[WS]).doubleValue();

			dataLine.add(d );
			dataLine.add( new Double(splitLine[Ta]).doubleValue());
			dataLine.add( new Double(splitLine[RH]).doubleValue());
			dataLine.add( ws);
			dataLine.add( new Double(splitLine[P]).doubleValue());
			dataLine.add( new Double(splitLine[Kd]).doubleValue());
			Double ldownDouble;

			if (mod_ldown.equals("Y"))
			{
				ldownDouble = ldModClass.ld_mod_(dataLine);
			}
			else
			{
				ldownDouble = new Double(splitLine[Ld]).doubleValue();
			}
			dataLine.add( ldownDouble);			
			metDataArrays.add(dataLine);
			
			count ++;
						
			
		
			
			
		}
	}

}
