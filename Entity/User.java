package Entity;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class User
{
    Admin admin = new Admin();
    Railway railway = new Railway();
    String trainName = "";
    String boardingPlace = "";
    String destinationPlace = "";
    int trainNumber = 0;
    int noOfSeats = 0;
    int catCum = 0;
    int trainTier = 0;
    String[] passengerName=new String[1000];
    int[] page=new int[1000];
    String[] pgen=new String[1000];

    Scanner input = new Scanner(System.in);

    int check1(int trainNumber) throws Exception
    {
        Class.forName("com.mysql.jdbc.Driver");
        Connection c= DriverManager.getConnection("jdbc:mysql://localhost:3306/railway?autoReconnect=true&useSSL=false","root","1234");
        Statement s=c.createStatement();
        ResultSet r=s.executeQuery("select * from train where tnum='"+trainNumber+"' ");
        if(r.first())
            return 1;
        else
            return 0;
    }

    public void inputReserve() throws Exception
    {
        System.out.print("Enter train number : ");
        trainNumber = input.nextInt();
        if(check1(trainNumber) == 0)
        {
            System.out.println("Train number doesn't exist");
            railway.user_mode();
        }

        System.out.print("Enter boarding : ");
        boardingPlace = input.next();

        System.out.print("Enter destination : ");
        destinationPlace = input.next();

        System.out.print("Number of seats required : ");
        noOfSeats = input.nextInt();
        java.sql.Date dateOfJourney = null;
        try
        {
            System.out.print("Enter date of train's journey in (yyyy-mm-dd) format : ");
            String dateInput = input.next();
            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateInput);
            dateOfJourney =  new java.sql.Date(date.getTime());
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        int j=0;
        int k=0;
        for(int i =0; i < noOfSeats ; i++)
        {
            System.out.print("Enter "+(i+1)+" passenger's name : ");
            passengerName[i] = input.next();

            System.out.print("Enter "+(i+1)+" passenger's age : ");
            page[i] = input.nextInt();
            if((page[i] > 0 && page[i] <= 12) || (page[i] >=60 && page[i]< 120))
                k++;
            if(page[i] <0 || page[i]> 120)
            {
                j = 1;
                System.out.println("Enter a valid age");
            }
            System.out.print("Enter "+(i+1)+" passenger's gender : ");
            pgen[i] = input.next();
        }
        if(j==1)
            return;

        System.out.println("Enter the class : ");
        System.out.println("1 - First AC");
        System.out.println("2 - Second AC");
        System.out.println("3 - Third AC");
        System.out.println("4 - Sleeper coach");
        trainTier = input.nextInt();
        if((trainTier != 1) && (trainTier != 2) && (trainTier != 3) && (trainTier != 4))
            System.out.println("Choose from above options only");

        else
        {
            String coach;
            if(trainTier == 1)  coach="First AC";
            else if(trainTier == 2)  coach="Second AC";
            else if(trainTier == 3)  coach="Third AC";
            else  coach="Sleeper Coach";

            System.out.print("Confirm there is no turning back!!(y/n) ");
            String conf = input.next();

            if(conf.equals("n"))
            {
                System.out.println("Your ticket is not booked");
                railway.user_mode();
            }
            int fare = reserve(trainNumber, trainName, boardingPlace, destinationPlace, noOfSeats, trainTier);
            if(fare == 0)
            {
                System.out.println("Train number doesn't exist");
                railway.user_mode();
            }
            System.out.println("Amount to be paid is "+(fare - ((k * (fare/noOfSeats))*0.5)));
            chart(passengerName, page, coach, trainNumber, dateOfJourney);
        }
    }
    int reserve(int trainNumber,String trainName,String boardingPlace,String destinationPlace,int noOfSeats,int trainTier)
    {
        int flag=0;
        int fare=0;
        try
        {

            Class.forName("com.mysql.jdbc.Driver");
            Connection c=DriverManager.getConnection("jdbc:mysql://localhost:3306/railway?autoReconnect=true&useSSL=false","root","1234");
            Statement s=c.createStatement();
            ResultSet r=s.executeQuery("select * from train");
            while(r.next())
            {
                if(trainNumber == r.getInt(1))
                {
                    flag=1;
                    if(trainTier == 1)
                        fare = noOfSeats * r.getInt(6);
                    else if(trainTier == 2)
                        fare = noOfSeats * r.getInt(7);
                    else if(trainTier == 3)
                        fare = noOfSeats * r.getInt(8);
                    else
                        fare = noOfSeats * r.getInt(9);
                    break;
                }
            }
            if(flag!=0)
            {
                PreparedStatement st=c.prepareStatement("update train set seats=seats-'"+noOfSeats+"' where tnum='"+trainNumber+"' ");
                st.execute();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        if(flag==0)
            return 0;
        else
            return fare;
    }
    void ticketReturn() throws Exception
    {
        ticket();
        System.out.print("Do you want to continue or return to main menu (y/n) respectively  : ");
        String ch = input.next();
        if(ch.equals("y"))
        {
            railway.user_mode();
        }
        else
        {
            railway.main_menu();
        }
    }

    void ticket()
    {
        try
        {


            Class.forName("com.mysql.jdbc.Driver");
            Connection c = DriverManager.getConnection("jdbc:mysql://localhost:3306/railway?autoReconnect=true&useSSL=false","root","1234");
            Statement s1 = c.createStatement();
            ResultSet r1 = s1.executeQuery("select * from chart order by sno desc limit 1");
            r1.first();
            Statement s2=c.createStatement();
            ResultSet r2=s2.executeQuery("select * from chart where pnr='"+r1.getLong(1)+"' ");
            r2.first();
            java.util.Date d=new java.util.Date();
            d.setTime(r2.getTimestamp(8).getTime());
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String str7 = df.format(d);

            DateFormat dF = new SimpleDateFormat("dd/MM/yyyy");
            String str8 = dF.format(r2.getDate(9));
            System.out.println("***************************************************************************************");
            System.out.println("PNR Number : "+r2.getLong(1)+"                        "+"Coach : "+r2.getString(6));
            System.out.println("Name : "+r2.getString(2)+"             "+"Age : "+r2.getInt(3)+"     "+"Gender : "+r2.getString(4));
            System.out.println("Status : "+r2.getString(7)+"                           "+"Seat Number : "+r2.getInt(5));
            while(r2.next())
            {
                System.out.println("Name : "+r2.getString(2)+"             "+"Age : "+r2.getInt(3)+"     "+"Gender : "+r2.getString(4));
                System.out.println("Status : "+r2.getString(7)+"                           "+"Seat Number : "+r2.getInt(5));

            }
            System.out.println("Date of Travelling : "+str8+"                    "+"Booked on : "+str7);
            System.out.println("***************************************************************************************");
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    void chart(String pname[],int page[],String coach,int trainNumber,java.sql.Date dateOfJournery)
    {
        try
        {

            java.util.Date date=new java.util.Date();
            java.sql.Timestamp sqt=new java.sql.Timestamp(date.getTime());
            Class.forName("com.mysql.jdbc.Driver");
            Connection c=DriverManager.getConnection("jdbc:mysql://localhost:3306/railway?autoReconnect=true&useSSL=false","root","1234");
            Statement s2=c.createStatement();
            Statement s1=c.createStatement();
            Statement s3=c.createStatement();
            ResultSet r3=s3.executeQuery("select * from chart order by sno desc limit 1");
            r3.first();
            for(int i=0; i<noOfSeats; i++)
            {
                ResultSet r2=s2.executeQuery("select * from chart order by sno desc limit 1");
                r2.first();
                ResultSet r1=s1.executeQuery("select * from train where tnum='"+trainNumber+"' and doj='"+dateOfJournery+"' ");
                r1.first();
                PreparedStatement st=c.prepareStatement("insert into chart (pnr,name,age,gender,seatno,coach,status,timestamp,dot,tnum) values(?,?,?,?,?,?,?,?,?,?)");
                st.setLong(1,r3.getLong(1)+1);
                st.setString(2,pname[i]);
                st.setInt(3,page[i]);
                st.setString(4,pgen[i]);
                if(r1.getInt(3)>0)			st.setInt(5,r2.getInt(5)+1);
                else						st.setInt(5,0);
                st.setString(6,coach);
                if(r1.getInt(3)>0)			st.setString(7,"confirmed");
                else						st.setString(7,"waiting");
                st.setTimestamp(8,sqt);
                st.setDate(9,r1.getDate(10));
                st.setInt(10,trainNumber);
                st.executeUpdate();
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
        System.out.println("Congrats!!!! Your ticket is booked. Have a nice day!!");
        try
        {
            ticketReturn();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    public void cancel1() throws Exception
    {
        cancel();
        System.out.print("Do you want to continue or return to main menu (y/n) respectively  : ");
        String ch = input.next();
        if(ch.equals("y"))
        {
            railway.user_mode();
        }
        else
        {
            railway.main_menu();
        }
    }


    void cancel() throws Exception
    {
        long pnr;
        String option = "cancel";
        Class.forName("com.mysql.jdbc.Driver");
        Connection c=DriverManager.getConnection("jdbc:mysql://localhost:3306/railway?autoReconnect=true&useSSL=false","root","1234");
        System.out.print("Enter PNR Number  ");
        pnr = input.nextLong();
        Statement stmt=c.createStatement();
        ResultSet r=stmt.executeQuery("select * from chart where pnr='"+pnr+"' ");
        if(r.first())
        {
            PreparedStatement st=c.prepareStatement("update chart set status='"+option+"' where pnr='"+pnr+"' ");
            st.executeUpdate();
        }
        else
        {
            System.out.println("PNR number does not exist ");
        }

    }
    void setw(int tnum, String str1, int seats,String str10,String str11, int fAc,int sAc,int tAc,int sc,java.sql.Date doj, String str7,String str9, int width)
    {
        String str=Integer.toString(tnum);
        System.out.print(str);
        for (int x = str.length(); x < width; ++x)
            System.out.print(' ');
        System.out.print(str1);
        for (int x = str1.length(); x < width; ++x)
            System.out.print(' ');
        String str8=Integer.toString(seats);
        System.out.print(str8);
        for (int x = str8.length(); x < width; ++x)
            System.out.print(' ');
        System.out.print(str10);
        for (int x = str10.length(); x < width; ++x)
            System.out.print(' ');
        System.out.print(str11);
        for (int x = str11.length(); x < width; ++x)
            System.out.print(' ');
        String str2=Integer.toString(fAc);
        System.out.print(str2);
        for (int x = str2.length(); x < width; ++x)
            System.out.print(' ');
        String str3=Integer.toString(sAc);
        System.out.print(str3);
        for (int x = str3.length(); x < width; ++x)
            System.out.print(' ');
        String str4=Integer.toString(tAc);
        System.out.print(str4);
        for (int x = str4.length(); x < width; ++x)
            System.out.print(' ');
        String str5=Integer.toString(sc);
        System.out.print(str5);
        for (int x = str5.length(); x < width; ++x)
            System.out.print(' ');

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        String str6 = df.format(doj);
        System.out.print(str6);
        for (int x = str6.length(); x < width; ++x)
            System.out.print(' ');
        System.out.print(str7);
        for (int x = str7.length(); x < width; ++x)
            System.out.print(' ');
        System.out.println(str9);
    }
    void enquiry1() throws Exception
    {
        enquiry();
        System.out.print("Do you want to continue or return to main menu (y/n) respectively  : ");
        String ch = input.next();
        if(ch.equals("y"))
        {
            railway.user_mode();
        }
        else
        {
            railway.main_menu();
        }
    }

    void enquiry() throws Exception
    {
        System.out.print("From ");
        String from = input.next();
        System.out.print("To ");
        String to = input.next();
        Class.forName("com.mysql.jdbc.Driver");
        Connection c=DriverManager.getConnection("jdbc:mysql://localhost:3306/railway?autoReconnect=true&useSSL=false","root","1234");
        System.out.println("***************************************************************************************************************************************************************************************");
        System.out.println("Train Number   Train Name     Seats          Boarding       Destination    First AC       Second AC      Third AC       Sleeper Coach  Journey date   Departure      Arrival");
        System.out.println("***************************************************************************************************************************************************************************************");

        Statement st=c.createStatement();
        ResultSet r=st.executeQuery("select * from train where bp='"+from+"' and dp='"+to+"' and doj>=CURDATE()");
        while(r.next())
        {
            //tnum*****tname****seats******bp*******dp********fAC****sAC*****tAC******sc***doj*******dtime***atime*****sno
            setw(r.getInt(1),r.getString(2),r.getInt(3),r.getString(4),r.getString(5),r.getInt(6),r.getInt(7),r.getInt(8),r.getInt(9),r.getDate(10),r.getString(11),r.getString(12),15);


        }
    }


}

