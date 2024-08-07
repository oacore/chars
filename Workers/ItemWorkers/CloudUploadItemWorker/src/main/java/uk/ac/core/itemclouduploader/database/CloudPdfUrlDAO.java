/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.ac.core.itemclouduploader.database;

/**
 *
 * @author mc26486
 */
/**
 *
 *
 * CREATE TABLE IF NOT EXISTS `cloud_pdf_url` ( `id_document` int(11) NOT NULL
 * PRIMARY KEY, `url` TEXT NOT NULL, `created` timestamp NOT NULL DEFAULT
 * CURRENT_TIMESTAMP )
 *
 * @author mc26486
 */
public interface CloudPdfUrlDAO {

    public Boolean save(Integer idDocument, String url);
}
