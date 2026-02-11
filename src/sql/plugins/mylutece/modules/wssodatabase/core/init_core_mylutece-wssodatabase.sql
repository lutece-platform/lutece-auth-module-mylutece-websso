--
-- Dumping data for table core_admin_right
--
INSERT INTO core_admin_right(id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES
('WSSODATABASE_MANAGEMENT_USERS','module.mylutece.wssodatabase.adminFeature.wssodatabase_management_user.name',3,'jsp/admin/plugins/mylutece/modules/wssodatabase/ManageUsers.jsp','module.mylutece.wssodatabase.adminFeature.wssodatabase_management_user.description',0,'mylutece-websso','USERS',NULL,NULL);

INSERT INTO core_admin_right(id_right,name,level_right,admin_url,description,is_updatable,plugin_name,id_feature_group,icon_url,documentation_url) VALUES
('WSSODATABASE_MANAGEMENT_PROFILS','module.mylutece.wssodatabase.adminFeature.wssodatabase_management_profil.name',3,'jsp/admin/plugins/mylutece/modules/wssodatabase/ManageProfils.jsp','module.mylutece.wssodatabase.adminFeature.wssodatabase_management_profil.description',0,'mylutece-websso','MANAGERS',NULL,NULL);

--
-- Dumping data for table core_user_right
--
INSERT INTO core_user_right(id_right,id_user) VALUES
('WSSODATABASE_MANAGEMENT_USERS',1);

INSERT INTO core_user_right(id_right,id_user) VALUES
('WSSODATABASE_MANAGEMENT_PROFILS',1);

INSERT INTO core_physical_file (file_value) VALUES ('<!-- export_myluteceWebSSO_users_xml.xml --><?xml version=\"1.0\"?>\r\n<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\r\n	<xsl:output method=\"text\"/>\r\n	\r\n	<xsl:template match=\"users\">\r\n		<xsl:apply-templates select=\"user\" />\r\n	</xsl:template>\r\n	\r\n	<xsl:template match=\"user\">\r\n		<xsl:text>\"</xsl:text>\r\n		<xsl:value-of select=\"guid\" />\r\n		<xsl:text>\";\"</xsl:text>\r\n		<xsl:value-of select=\"last_name\" />\r\n		<xsl:text>\";\"</xsl:text>\r\n		<xsl:value-of select=\"first_name\" />\r\n		<xsl:text>\";\"</xsl:text>\r\n		<xsl:value-of select=\"email\" />\r\n		<xsl:text>\"</xsl:text>\r\n		<xsl:apply-templates select=\"roles\" />\r\n		<xsl:apply-templates select=\"groups\" />\r\n		<xsl:apply-templates select=\"profils\" />\r\n		<xsl:text>&#10;</xsl:text>\r\n	</xsl:template>\r\n	\r\n	<xsl:template match=\"roles\">\r\n		<xsl:apply-templates select=\"role\" />\r\n	</xsl:template>\r\n	\r\n	<xsl:template match=\"role\">\r\n		<xsl:text>;\"role:</xsl:text>\r\n		<xsl:value-of select=\"current()\" />\r\n		<xsl:text>\"</xsl:text>\r\n	</xsl:template>\r\n	\r\n	<xsl:template match=\"groups\">\r\n		<xsl:apply-templates select=\"group\" />\r\n	</xsl:template>\r\n	\r\n	<xsl:template match=\"group\">\r\n		<xsl:text>;\"group:</xsl:text>\r\n		<xsl:value-of select=\"current()\" />\r\n		<xsl:text>\"</xsl:text>\r\n	</xsl:template>\r\n	\r\n	<xsl:template match=\"profils\">\r\n		<xsl:apply-templates select=\"profil\" />\r\n	</xsl:template>\r\n	\r\n	<xsl:template match=\"profil\">\r\n		<xsl:text>;\"profil:</xsl:text>\r\n		<xsl:value-of select=\"current()\" />\r\n		<xsl:text>\"</xsl:text>\r\n	</xsl:template>\r\n	\r\n</xsl:stylesheet>');
INSERT INTO core_file (title, id_physical_file, file_size, mime_type) VALUES ('export_myluteceWebSSO_users_xml.xml', (select id_physical_file from core_physical_file where file_value like '<!-- export_myluteceWebSSO_users_xml.xml -->%' )  ,1861,'application/xml');
INSERT INTO core_xsl_export  (title, description, extension, id_file, plugin) VALUES ('MyLutece Web Sso - Export CSV des utilisateurs','Export des utilisateur MyLutece Web Sso dans un fichier CSV','csv', (select id_file from core_file where title = 'export_myluteceWebSSO_users_xml.xml' ) ,'mylutece-websso');
