import com.alibaba.fastjson.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;


@WebServlet(name = "matchs", urlPatterns =  "/matchs")

public class match extends HttpServlet {
    private static final long  serialVersionUID = 1L;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doPost(request, response);

    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,IOException{
        //doGet(request,response);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");


        try {
            getMatch(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void getMatch(HttpServletRequest request, HttpServletResponse response) throws SQLException, Exception{
        String username = request.getParameter("username");
        PrintWriter check = response.getWriter();

        info_im serv = new info_im();
        int id = serv.getId(username);
        String str1 = serv.getLabel(id);

        Connection conn = DButil.getConnection();
        String sql = "" + "select * from user_info";
        PreparedStatement psmt = conn.prepareStatement(sql);
        ResultSet rs = psmt.executeQuery();

        /// int count =0;
        //List<String> list = new ArrayList<String>();
        List<Integer> user_id = new ArrayList<Integer>();
        List<Double> res = new ArrayList<Double>();
        List<Double> sec = new ArrayList<>();
        while (rs.next()){
            if (rs.getInt("id") == id){
                continue;
            }else {
                user_id.add(rs.getInt("id"));
                String str2 =  rs.getString("label");
                //list.add(str2);
                double temp = serv.getSimilar(str1,str2);
                res.add(temp);
                sec.add(temp);
                //System.out.println(user_id.get(count)+"="+res.get(count++));
            }
        }
        Collections.sort(sec);
        int top = res.indexOf(Collections.max(res));
        int second = res.indexOf(sec.get(sec.size()-2));
        int best1 = user_id.get(top); // best match
        int best2 = user_id.get(second);
        //System.out.println(best1);
        //System.out.println(best2);


        //System.out.print(top);
        //System.out.print(second);
        if (top!=-1 && second!=-1 && top!=second){
            User matchPerson1 = serv.getUser_id(best1);
            User matchPerson2 = serv.getUser_id(best2);

            JSONObject re = new JSONObject();
            re.put("user1", matchPerson1.getUsername());
            re.put("user2", matchPerson2.getUsername());

           //String info = matchPerson1.getUsername() + "###"+ matchPerson2.getUsername();
            //System.out.println(info);
            check.write(re.toJSONString());
            check.close();

        }
        else {
            JSONObject re = new JSONObject();
            re.put("fail", 0);

            check.write(re.toJSONString());
            check.close();
        }






    }

}