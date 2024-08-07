SELECT 
    rep.id_repository
FROM
    repository rep
WHERE
    r.metadata_format= = 'rioxx'
        AND r.id.repository NOT IN 
        (SELECT DISTINCT up.id_repository
        FROM
            `update` up
		INNER JOIN
            repository r ON (up.id_repository = r.id_repository)
        WHERE
            up.operation = 'metadata_download'
                AND up.status = 'successful'
                AND r.id_repository NOT IN (143 , 144, 645, 153, 150)
                AND r.disabled = 0
                AND TIMESTAMPDIFF(DAY,
                last_update_time,
                NOW()) > 7
                AND r.metadata_format = 'rioxx'
        ORDER BY up.last_update_time ASC
        )
