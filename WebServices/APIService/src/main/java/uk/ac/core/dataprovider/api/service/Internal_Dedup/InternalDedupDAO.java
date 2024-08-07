package uk.ac.core.dataprovider.api.service.Internal_Dedup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.ac.core.dataprovider.api.model.internal_dedup.DuplicateItem;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class InternalDedupDAO {
    private static final Logger log = LoggerFactory.getLogger(InternalDedupDAO.class);

    @Autowired
    JdbcTemplate jdbcTemplate;

    private static final String INTERNAL_DUPLICATES_QUERY =
            "SELECT DISTINCT d.oai, wtd2.document_id, wtd2.work_id, cnt, sq.confidence, d.id_repository,\n" +
                    "       dm.title, d.oai, dm.authors, dm.date, d.doc_class\n" +
                    "FROM (\n" +
                    "         SELECT *, COUNT(wtd1.work_id) as cnt\n" +
                    "         FROM work_to_document wtd1\n" +
                    "                  JOIN document d1 ON d1.id_document = wtd1.document_id\n" +
                    "         WHERE d1.id_repository = ? AND d1.deleted = 0\n" +
                    "         GROUP BY wtd1.work_id\n" +
                    "     ) sq\n" +
                    "         INNER JOIN work_to_document wtd2 ON sq.work_id = wtd2.work_id\n" +
                    "         JOIN document d ON d.id_document = wtd2.document_id\n" +
                    "         JOIN document_metadata dm on d.id_document = dm.id_document\n" +
                    "WHERE cnt > 1 and sq.confidence > ? and dm.title is not null and d.oai is not null and d.deleted=0;";

    public List<DuplicateItem> getInternalDuplicates(int idRepository, double confidence) {
        log.info("Getting information from DB ...");
        this.jdbcTemplate.setQueryTimeout(300000);
        List<DuplicateItem> duplicates = jdbcTemplate.query(
                INTERNAL_DUPLICATES_QUERY,
                (rs, rowNum) -> {
                    DuplicateItem di = new DuplicateItem();
                    di.setDocumentId(rs.getInt("document_id"));
                    di.setWorkId(rs.getInt("work_id"));
                    di.setCount(rs.getInt("cnt"));
                    di.setIdRepository(rs.getInt("id_repository"));
                    di.setConfidence(rs.getDouble("confidence"));
                    di.setTitle(rs.getString("title"));
                    di.setOai(rs.getString("oai"));
                    di.setPublicationDate(rs.getString("date"));
                    di.setDocClass(rs.getString("doc_class"));

                    String authors = rs.getString("authors");
                    List<String> authorsList = new ArrayList<>();
                    if (authors != null) {
                        authorsList = Stream.of(authors.split("#-#-#"))
                                .map(String::trim)
                                .collect(Collectors.toList());
                    } else {
                        log.error("`authors` field is null for document {}", di.getDocumentId());
                    }

                    di.setAuthors(authorsList);

                    return di;
                },
                idRepository,
                confidence

        );
        log.info("Got {} rows", duplicates.size());

        return duplicates;
    }
}
