DELETE FROM core_physical_file WHERE id_physical_file = 129;
INSERT INTO core_physical_file VALUES (129,e'<?xml version="1.0" encoding="UTF-8"?><xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"><xsl:output method="text" /><xsl:template match="users"><xsl:apply-templates select="user" /></xsl:template><xsl:template match="user"><xsl:text>"</xsl:text><xsl:value-of select="guid" /><xsl:text>";"</xsl:text><xsl:value-of select="last_name" /><xsl:text>";"</xsl:text><xsl:value-of select="first_name" /><xsl:text>";"</xsl:text><xsl:value-of select="email" /><xsl:text>";"</xsl:text><xsl:value-of select="date" /><xsl:text>"</xsl:text><xsl:apply-templates select="roles" /><xsl:apply-templates select="groups" /><xsl:apply-templates select="profils" /><xsl:text /><xsl:text>&#10;</xsl:text></xsl:template><xsl:template match="roles"><xsl:apply-templates select="role" /></xsl:template><xsl:template match="role"><xsl:text>;"role:</xsl:text><xsl:value-of select="current()" /><xsl:text>"</xsl:text></xsl:template><xsl:template match="groups"><xsl:apply-templates select="group" /></xsl:template><xsl:template match="group"><xsl:text>;"group:</xsl:text><xsl:value-of select="current()" /><xsl:text>"</xsl:text></xsl:template><xsl:template match="profils"><xsl:apply-templates select="profil" /></xsl:template><xsl:template match="profil"><xsl:text>;"profil:</xsl:text><xsl:value-of select="current()" /><xsl:text>"</xsl:text></xsl:template></xsl:stylesheet>');
