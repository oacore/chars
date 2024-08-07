package uk.ac.core.database.model.mappers;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import uk.ac.core.common.model.legacy.Language;

/**
 *
 * @author lucasanastasiou
 */
public class LanguageMapper implements RowMapper<Language> {

    @Override
    public Language mapRow(ResultSet rs, int i) throws SQLException {
        Language language = new Language();
        language.setLanguageId(rs.getInt("id_language"));
        language.setCode(rs.getString("code"));
        language.setName(rs.getString("name"));
        return language;
    }
}
