package dao;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class PersonDAOImpl implements PersonDAO {

    private String JDBC_URL;
    private String JDBC_USERNAME;
    private String JDBC_PASSWORD;

    private final String GET_BY_USER_ID 			= "select us00.CLID, us00.USID, us00.USNM, us00.USPS, us00.ACTIVE, us00.SYID, bp00.BPID, isnull(ltrim(bp00.BMAIL),bp00.PMAIL) as BMAIL, bp00.CPTP,us00.PSRS from SAPSR3.[/BNT/US00] us00  inner join SAPSR3.[/BNT/BP00] bp00 on bp00.CLID=us00.CLID and bp00.BPID = us00.USID and bp00.ACTIVE='X' and isnull(ltrim(bp00.BMAIL),ltrim(bp00.PMAIL)) is not null WHERE us00.CLID = ?1 and us00.SYID = ?2 and UPPER (us00.USID) = UPPER(?3) and us00.ACTIVE = 'X'";
    private final String GET_BY_USERNAME 			= "select us00.CLID, us00.USID, us00.USNM, us00.USPS, us00.ACTIVE, us00.SYID, bp00.BPID, isnull(ltrim(bp00.BMAIL),bp00.PMAIL) as BMAIL, bp00.CPTP,us00.PSRS from SAPSR3.[/BNT/US00] us00  inner join SAPSR3.[/BNT/BP00] bp00 on bp00.CLID=us00.CLID and bp00.BPID = us00.USID and bp00.ACTIVE='X' and isnull(ltrim(bp00.BMAIL),ltrim(bp00.PMAIL)) is not null WHERE us00.CLID = ?1 and us00.SYID = ?2 and UPPER (us00.USNM) = UPPER(?3) and us00.ACTIVE = 'X'";
    private final String GET_BY_EMAIL				= "select us00.CLID, us00.USID, us00.USNM, us00.USPS, us00.ACTIVE, us00.SYID, bp00.BPID, isnull(ltrim(bp00.BMAIL),bp00.PMAIL) as BMAIL, bp00.CPTP,us00.PSRS from SAPSR3.[/BNT/US00] us00  inner join SAPSR3.[/BNT/BP00] bp00 on bp00.CLID=us00.CLID and bp00.BPID = us00.USID and bp00.ACTIVE='X' and isnull(ltrim(bp00.BMAIL),ltrim(bp00.PMAIL)) is not null WHERE us00.CLID = ?1 and us00.SYID = ?2 and UPPER (isnull(ltrim(bp00.BMAIL),bp00.PMAIL)) = UPPER(?3) and us00.ACTIVE = 'X'";
    private final String FIND_USER_CSID 			= "select us00.USNM, us00.USPS, CSID = (case when us00.CSID is null or ltrim(us00.CSID) = '' then us00.SYID else us00.CSID end) FROM SAPSR3.[/BNT/US00] us00 WHERE us00.CLID = ?1 and us00.SYID = ?2 and UPPER (us00.USNM) = UPPER (?3) and us00.ACTIVE = 'X'";
    private final String USER_AUTH_SYSTEM 		= "select sy01_t.SYTP, sy01_t.SYPCF,sy01_t.SYURL from SAPSR3.[/BNT/SY01] sy01 inner join SAPSR3.[/BNT/SY01] sy01_t on sy01_t.CLID = sy01.CLID and sy01_t.SYID = sy01.SYID where sy01.CLID = ?1 and sy01.SYID = ?2";
    private final String UPDATE_USER_PASSWORD 	= "update SAPSR3.[/BNT/US00] set USPS = ?1 where CLID = ?2 and  SYID = ?3 and UPPER(USNM) = UPPER(?4)";

    private final String TESTALLUSER             ="select TOP 400 us00.CLID,us00.USID,us00.SYID,us00.USNM,us00.USPS,us00.ACTIVE,bp00.BPID, isnull(ltrim(bp00.BMAIL),bp00.PMAIL) as BMAIL,bp00.CPTP,us00.PSRS from SAPSR3.[/BNT/US00] us00  inner join SAPSR3.[/BNT/BP00] bp00 on bp00.CLID=us00.CLID and bp00.BPID = us00.USID and isnull(ltrim(bp00.BMAIL),ltrim(bp00.PMAIL)) is not null WHERE us00.CLID=?1 and us00.ACTIVE = 'X'";
    private final String SEARCH_FOR_USER         ="select us00.CLID,us00.USID,us00.USNM,us00.USPS,us00.ACTIVE,us00.SYID,bp00.BPID, isnull(ltrim(bp00.BMAIL),bp00.PMAIL) as BMAIL,bp00.CPTP,us00.PSRS from SAPSR3.[/BNT/US00] us00  inner join SAPSR3.[/BNT/BP00] bp00 on bp00.CLID=us00.CLID and bp00.BPID = us00.USID and bp00.ACTIVE='X' and isnull(ltrim(bp00.BMAIL),ltrim(bp00.PMAIL)) is not null WHERE  us00.CLID=?1 and us00.ACTIVE = 'X' and UPPER(us00.USNM) like ?2 order by us00.USID";


    private Connection connection;

    public PersonDAOImpl() {
        setConnection();
    }

    public Connection getConnection() {
        return connection;
    }

    private void setConnection() {
        Properties prop = new Properties();
        InputStream input = null;
        try {
            input = ClassLoader.getSystemClassLoader().getResourceAsStream("database.properties");
            prop.load(input);

        } catch (IOException io) {
            io.printStackTrace();
        }

        JDBC_URL=prop.getProperty("jdbc.url");
        JDBC_USERNAME=prop.getProperty("jdbc.username");
        JDBC_PASSWORD=prop.getProperty("jdbc.password");


        Properties properties=new Properties();
        properties.setProperty("user",JDBC_USERNAME);
        properties.setProperty("password",JDBC_PASSWORD);
        properties.setProperty("useUnicode","yes");
        properties.setProperty("characterEncoding","UTF-8");
        properties.setProperty("serverTimezone","UTC");
        properties.setProperty("autoReconnect","true");
        properties.setProperty("useSSL","false");

        try {
            connection= DriverManager.getConnection(JDBC_URL,properties);
        } catch (Exception e) {
            System.out.println("Error : "+e);
        }
    }

    public void getUserByBpid(String CLID, String SYID, String USID) {
        try {
            ResultSet resultSet=executeQuery(GET_BY_USER_ID,CLID,SYID,USID);
            if(resultSet.next()){
                    String id=resultSet.getString("BMAIL");
                System.out.println(id);
            }
        } catch (Exception e) {
            System.out.println("Error : "+e);
        }
    }

    private ResultSet executeQuery(String sql,Object... queryParameters){
        try {
            PreparedStatement ps=connection.prepareStatement(sql);
            int index=1;
            if(queryParameters!=null){
                for(Object paramater:queryParameters){
                    ps.setObject(index++,paramater);
                }
            }
            ResultSet resultSet=ps.executeQuery();
            return resultSet;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;


    }


    public static void main(String[] args) {
        PersonDAOImpl personDAO=new PersonDAOImpl();
        personDAO.getUserByBpid("100","BNET100","00001129");

    }
}
