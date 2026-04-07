-- liquibase formatted sql
-- changeset mylutece-websso:init_core_mylutece-wssodatabase.sql
-- preconditions onFail:MARK_RAN onError:WARN
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
