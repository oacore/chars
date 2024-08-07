/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.database.service.control.tool;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import org.springframework.stereotype.Service;

/**
 *
 * @author Vaclav Bayer, vb4826@open.ac.uk
 */
@Service
public class DBControlTools {
    
    public DBControlTools(){
    }
    
    public String createNullCheckedClause(String column, Object value) {
        String operator = (value == null ? "is" : "=");
        return String.format("(%s %s ?)", column, operator);
    }
    
    public void checkStatementProperty(PreparedStatement ps, int positionNum, String property) throws SQLException{
        if(property == null || property.isEmpty()){
            ps.setNull(positionNum, Types.VARCHAR);
        }else{
            ps.setString(positionNum, property);
        }
    }
    
    public void checkStatementProperty(PreparedStatement ps, int positionNum, Integer property) throws SQLException{
        if(property == null){
            ps.setNull(positionNum, Types.INTEGER);
        }else{
            ps.setInt(positionNum, property);
        }
    }
    
}
