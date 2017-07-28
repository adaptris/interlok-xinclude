<?xml version="1.0" encoding="utf-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="duplicates"/>
  <xsl:output method="xml" indent="yes"/>
  <xsl:strip-space elements="*"/>

  <xsl:template match="standard-workflow[unique-id='workflow']">
    <xsl:call-template name="duplicate-workflow">
      <xsl:with-param name="var" select="$duplicates"/>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="standard-workflow/unique-id[.='workflow']" />

  <xsl:template name="duplicate-workflow">
    <xsl:param name="var"/>
    <xsl:param name="c" select="1"/>
    <xsl:choose>
      <xsl:when test="$var > 0">
        <xsl:variable name="uniqueId" select="unique-id" />
        <xsl:element name="{local-name()}">
          <xsl:element name="unique-id">
            <xsl:value-of select="concat($uniqueId, '-', $c)" />
          </xsl:element>
          <xsl:apply-templates select="@*|node()"/>
        </xsl:element>
        <xsl:call-template name="duplicate-workflow">
          <xsl:with-param name="var" select="$var - 1"/>
          <xsl:with-param name="c" select="$c + 1"/>
        </xsl:call-template>
      </xsl:when>
      <xsl:otherwise/>
    </xsl:choose>
  </xsl:template>
  
  <xsl:template match="@*|node()">
    <xsl:copy>
      <xsl:apply-templates select="@*|node()"/>
    </xsl:copy>
  </xsl:template>

</xsl:stylesheet>