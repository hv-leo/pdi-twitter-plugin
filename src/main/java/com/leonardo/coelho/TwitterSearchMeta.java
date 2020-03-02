/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.leonardo.coelho;

import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaDate;
import org.pentaho.di.core.row.value.ValueMetaString;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;
import twitter4j.Query.ResultType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Skeleton for PDI Step plugin.
 */
@Step( id = "TwitterSearch", image = "TwitterSearch.svg", name = "Twitter Search",
    description = "Search for Tweets.", categoryDescription = "Input" )
public class TwitterSearchMeta extends BaseStepMeta implements StepMetaInterface {
  private static Class<?> PKG = TwitterSearch.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$

  private static final Map<String, String> supportedLanguages;
  static {
    supportedLanguages = new HashMap<String, String>()
      {
        {
          put( "French", "fr" );
          put( "English", "en" );
          put( "Arabic", "ar" );
          put( "Japanese", "ja" );
          put( "Spanish", "es" );
          put( "German", "de" );
          put( "Italian", "it" );
          put( "Indonesian", "id" );
          put( "Portuguese", "pt" );
          put( "Korean", "ko" );
          put( "Turkish", "tr" );
          put( "Russian", "ru" );
          put( "Dutch", "nl" );
          put( "Filipino", "fil" );
          put( "Malay", "msa" );
          put( "Traditional Chinese", "zh-tw" );
          put( "Simplified Chinese", "zh-cn" );
          put( "Hindi", "hi" );
          put( "Norwegian", "no" );
          put( "Swedish", "sv" );
          put( "Finnish", "fi" );
          put( "Danish", "da" );
          put( "Polish", "pl" );
          put( "Hungarian", "hu" );
          put( "Persian", "fa" );
          put( "Hebrew", "he" );
          put( "Urdu", "ur" );
          put( "Thai", "th" );
          put( "Ukrainian", "uk" );
          put( "Catalan", "ca" );
          put( "Irish", "ga" );
          put( "Greek", "el" );
          put( "Basque", "eu" );
          put( "Czech", "cs" );
          put( "Galician", "gl" );
          put( "Romanian", "ro" );
          put( "Croatian", "hr" );
          put( "British English", "en-gb" );
          put( "Vietnamese", "vi" );
          put( "Bangla", "bn" );
          put( "Bulgarian", "bg" );
          put( "Serbian", "sr" );
          put( "Slovak", "sk" );
          put( "Gujarati", "gu" );
          put( "Marathi", "mr" );
          put( "Tamil", "ta" );
          put( "Kannada", "kn" );
        }
      };
  }
    
  private String searchQueryField;
  private ResultType resultTypeField;
  private String langField;
  private String langCodeField;
  private boolean sensitiveField;
  private boolean onlyVerifiedUsersField;
  private String consumerKeyField;
  private String consumerSecretField;
  private String accessTokenKeyField;
  private String accessTokenSecretField;
  private String creationDateField;
  private String authorField;
  private String authorScreenNameField;
  private String authorLocationField;
  private String tweetBodyField;

  public TwitterSearchMeta() {
    super(); // allocate BaseStepMeta
  }

  public void loadXML( Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore ) throws KettleXMLException {
    readData( stepnode );
  }

  public Object clone() {
    Object retval = super.clone();
    return retval;
  }

  private void readData( Node stepnode ) {
    searchQueryField = XMLHandler.getTagValue( stepnode, "searchQueryField" );
    resultTypeField = ResultType.valueOf( XMLHandler.getTagValue( stepnode, "resultTypeField" ) );
    langField = XMLHandler.getTagValue( stepnode, "langField" );
    langCodeField = XMLHandler.getTagValue( stepnode, "langCodeField" );
    sensitiveField = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepnode, "sensitiveField" ) );
    onlyVerifiedUsersField = "Y".equalsIgnoreCase( XMLHandler.getTagValue( stepnode, "onlyVerifiedUsersField" ) );
    consumerKeyField = XMLHandler.getTagValue( stepnode, "consumerKeyField" );
    consumerSecretField = XMLHandler.getTagValue( stepnode, "consumerSecretField" );
    accessTokenKeyField = XMLHandler.getTagValue( stepnode, "accessTokenKeyField" );
    accessTokenSecretField = XMLHandler.getTagValue( stepnode, "accessTokenSecretField" );
    creationDateField = XMLHandler.getTagValue( stepnode, "creationDateField" );
    authorField = XMLHandler.getTagValue( stepnode, "authorField" );
    authorScreenNameField = XMLHandler.getTagValue( stepnode, "authorScreenNameField" );
    authorLocationField = XMLHandler.getTagValue( stepnode, "authorLocationField" );
    tweetBodyField = XMLHandler.getTagValue( stepnode, "tweetBodyField" );
  }

  public void setDefault() {
    tweetBodyField = "tweet";
  }

  public void readRep( Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases ) throws KettleException {
    try {
      searchQueryField = rep.getStepAttributeString( id_step, "searchQueryField" );
      resultTypeField = ResultType.valueOf( rep.getStepAttributeString( id_step, "resultTypeField" ) );
      langField = rep.getStepAttributeString( id_step, "langField" );
      langCodeField = rep.getStepAttributeString( id_step, "langCodeField" );
      sensitiveField = "Y".equalsIgnoreCase( rep.getStepAttributeString( id_step, "sensitiveField" ) );
      onlyVerifiedUsersField = "Y".equalsIgnoreCase( rep.getStepAttributeString( id_step, "onlyVerifiedUsersField" ) );
      consumerKeyField = rep.getStepAttributeString( id_step, "consumerKeyField" );
      consumerSecretField = rep.getStepAttributeString( id_step, "consumerSecretField" );
      accessTokenKeyField = rep.getStepAttributeString( id_step, "accessTokenKeyField" );
      accessTokenSecretField = rep.getStepAttributeString( id_step, "accessTokenSecretField" );
      creationDateField = rep.getStepAttributeString( id_step, "creationDateField" );
      authorField = rep.getStepAttributeString( id_step, "authorField" );
      authorScreenNameField = rep.getStepAttributeString( id_step, "authorScreenNameField" );
      authorLocationField = rep.getStepAttributeString( id_step, "authorLocationField" );
      tweetBodyField = rep.getStepAttributeString( id_step, "tweetBodyField" );
    } catch ( Exception e ) {
      throw new KettleException( BaseMessages.getString(
        PKG, "TwitterSearchMeta.Exception.UnexpectedErrorInReadingStepInfoFromRepository", id_step ), e );
    }
  }

  @Override
  public String getXML() {
    StringBuilder retval = new StringBuilder();
    retval.append( "    " + XMLHandler.addTagValue( "searchQueryField", searchQueryField ) );
    retval.append( "    " + XMLHandler.addTagValue( "resultTypeField", resultTypeField.toString() ) );
    retval.append( "    " + XMLHandler.addTagValue( "langField", langField ) );
    retval.append( "    " + XMLHandler.addTagValue( "langCodeField", langCodeField ) );
    retval.append( "    " + XMLHandler.addTagValue( "sensitiveField", sensitiveField ) );
    retval.append( "    " + XMLHandler.addTagValue( "onlyVerifiedUsersField", onlyVerifiedUsersField ) );
    retval.append( "    " + XMLHandler.addTagValue( "consumerKeyField", consumerKeyField ) );
    retval.append( "    " + XMLHandler.addTagValue( "consumerSecretField", consumerSecretField ) );
    retval.append( "    " + XMLHandler.addTagValue( "accessTokenKeyField", accessTokenKeyField ) );
    retval.append( "    " + XMLHandler.addTagValue( "accessTokenSecretField", accessTokenSecretField ) );
    retval.append( "    " + XMLHandler.addTagValue( "creationDateField", creationDateField ) );
    retval.append( "    " + XMLHandler.addTagValue( "authorField", authorField ) );
    retval.append( "    " + XMLHandler.addTagValue( "authorScreenNameField", authorScreenNameField ) );
    retval.append( "    " + XMLHandler.addTagValue( "authorLocationField", authorLocationField ) );
    retval.append( "    " + XMLHandler.addTagValue( "tweetBodyField", tweetBodyField ) );
    return retval.toString();
  }

  public void saveRep( Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step )
    throws KettleException {
    try {
      rep.saveStepAttribute( id_transformation, id_step, "searchQueryField", searchQueryField );
      rep.saveStepAttribute( id_transformation, id_step, "resultTypeField", resultTypeField.toString() );
      rep.saveStepAttribute( id_transformation, id_step, "langField", langField );
      rep.saveStepAttribute( id_transformation, id_step, "langCodeField", langCodeField );
      rep.saveStepAttribute( id_transformation, id_step, "sensitiveField", sensitiveField );
      rep.saveStepAttribute( id_transformation, id_step, "onlyVerifiedUsersField", onlyVerifiedUsersField );
      rep.saveStepAttribute( id_transformation, id_step, "consumerKeyField", consumerKeyField );
      rep.saveStepAttribute( id_transformation, id_step, "consumerSecretField", consumerSecretField );
      rep.saveStepAttribute( id_transformation, id_step, "accessTokenKeyField", accessTokenKeyField );
      rep.saveStepAttribute( id_transformation, id_step, "accessTokenSecretField", accessTokenSecretField );
      rep.saveStepAttribute( id_transformation, id_step, "creationDateField", creationDateField );
      rep.saveStepAttribute( id_transformation, id_step, "authorField", authorField );
      rep.saveStepAttribute( id_transformation, id_step, "authorScreenNameField", authorScreenNameField );
      rep.saveStepAttribute( id_transformation, id_step, "authorLocationField", authorLocationField );
      rep.saveStepAttribute( id_transformation, id_step, "tweetBodyField", tweetBodyField );
    } catch ( Exception e ) {
      throw new KettleException( BaseMessages.getString(
        PKG, "TwitterSearchMeta.Exception.UnableToSaveStepInfoToRepository", id_step ), e );
    }
  }

  public void getFields( RowMetaInterface rowMeta, String origin, RowMetaInterface[] info, StepMeta nextStep,
    VariableSpace space, Repository repository, IMetaStore metaStore ) throws KettleStepException {
    ValueMetaInterface valueMeta;

    if ( !Utils.isEmpty( creationDateField ) ) {
      valueMeta = new ValueMetaDate( creationDateField );
      valueMeta.setOrigin( origin );
      rowMeta.addValueMeta( valueMeta );
    }

    if ( !Utils.isEmpty( authorField ) ) {
      valueMeta = new ValueMetaString( authorField );
      valueMeta.setOrigin( origin );
      rowMeta.addValueMeta( valueMeta );
    }

    if ( !Utils.isEmpty( authorScreenNameField ) ) {
      valueMeta = new ValueMetaString( authorScreenNameField );
      valueMeta.setOrigin( origin );
      rowMeta.addValueMeta( valueMeta );
    }

    if ( !Utils.isEmpty( authorLocationField ) ) {
      valueMeta = new ValueMetaString( authorLocationField );
      valueMeta.setOrigin( origin );
      rowMeta.addValueMeta( valueMeta );
    }

    if ( !Utils.isEmpty( tweetBodyField ) ) {
      valueMeta = new ValueMetaString( tweetBodyField );
      valueMeta.setOrigin( origin );
      rowMeta.addValueMeta( valueMeta );
    }
  }
  
  public void check( List<CheckResultInterface> remarks, TransMeta transMeta, 
    StepMeta stepMeta, RowMetaInterface prev, String[] input, String[] output,
    RowMetaInterface info, VariableSpace space, Repository repository, 
    IMetaStore metaStore ) {
    CheckResult cr;
    if ( prev == null || prev.size() == 0 ) {
      cr = new CheckResult( CheckResultInterface.TYPE_RESULT_WARNING, BaseMessages.getString( PKG, "TwitterSearchMeta.CheckResult.NotReceivingFields" ), stepMeta ); 
      remarks.add( cr );
    } else {
      cr = new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString( PKG, "TwitterSearchMeta.CheckResult.StepRecevingData", prev.size() + "" ), stepMeta );  
      remarks.add( cr );
    }
    
    // See if we have input streams leading to this step!
    if ( input.length > 0 ) {
      cr = new CheckResult( CheckResultInterface.TYPE_RESULT_OK, BaseMessages.getString( PKG, "TwitterSearchMeta.CheckResult.StepRecevingData2" ), stepMeta ); 
      remarks.add( cr );
    } else {
      cr = new CheckResult( CheckResultInterface.TYPE_RESULT_ERROR, BaseMessages.getString( PKG, "TwitterSearchMeta.CheckResult.NoInputReceivedFromOtherSteps" ), stepMeta ); 
      remarks.add( cr );
    }
  }
  
  public StepInterface getStep( StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta tr, Trans trans ) {
    return new TwitterSearch( stepMeta, stepDataInterface, cnr, tr, trans );
  }
  
  public StepDataInterface getStepData() {
    return new TwitterSearchData();
  }

  public String getDialogClassName() {
    return "com.leonardo.coelho.TwitterSearchDialog";
  }

  public String getSearchQueryField() {
    return searchQueryField;
  }

  public void setSearchQueryField( String searchQueryField ) {
    this.searchQueryField = searchQueryField;
  }

  public String getConsumerKeyField() {
    return consumerKeyField;
  }

  public void setConsumerKeyField( String consumerKeyField ) {
    this.consumerKeyField = consumerKeyField;
  }

  public String getConsumerSecretField() {
    return consumerSecretField;
  }

  public void setConsumerSecretField( String consumerSecretField ) {
    this.consumerSecretField = consumerSecretField;
  }

  public String getAccessTokenKeyField() {
    return accessTokenKeyField;
  }

  public void setAccessTokenKeyField( String accessTokenKeyField ) {
    this.accessTokenKeyField = accessTokenKeyField;
  }

  public String getAccessTokenSecretField() {
    return accessTokenSecretField;
  }

  public void setAccessTokenSecretField( String accessTokenSecretField ) {
    this.accessTokenSecretField = accessTokenSecretField;
  }

  public ResultType getResultTypeField() {
    return resultTypeField;
  }

  public void setResultTypeField( String resultTypeField ) {
    this.resultTypeField = ResultType.valueOf( resultTypeField );
  }

  public String getLangField() {
    return langField;
  }

  public String getLangCodeField() {
    return langCodeField;
  }

  public void setLangField( String langField ) {
    this.langField = langField;
    this.langCodeField = supportedLanguages.get( langField );
  }

  public boolean isSensitiveField() {
    return sensitiveField;
  }

  public void setSensitiveField( boolean sensitiveField ) {
    this.sensitiveField = sensitiveField;
  }

  public boolean isOnlyVerifiedUsersField() {
    return onlyVerifiedUsersField;
  }

  public void setOnlyVerifiedUsersField( boolean onlyVerifiedUsersField ) {
    this.onlyVerifiedUsersField = onlyVerifiedUsersField;
  }

  public String[] getSupportedLanguages() {
    String[] languages = supportedLanguages.keySet().toArray( new String[0] );
    Arrays.sort( languages );
    return languages;
  }

  public String getCreationDateField() {
    return creationDateField;
  }

  public void setCreationDateField( String creationDateField ) {
    this.creationDateField = creationDateField;
  }

  public String getAuthorField() {
    return authorField;
  }

  public void setAuthorField( String authorField ) {
    this.authorField = authorField;
  }

  public String getAuthorScreenNameField() {
    return authorScreenNameField;
  }

  public void setAuthorScreenNameField( String authorScreenNameField ) {
    this.authorScreenNameField = authorScreenNameField;
  }

  public String getAuthorLocationField() {
    return authorLocationField;
  }

  public void setAuthorLocationField( String authorLocationField ) {
    this.authorLocationField = authorLocationField;
  }

  public String getTweetBodyField() {
    return tweetBodyField;
  }

  public void setTweetBodyField( String tweetBodyField ) {
    this.tweetBodyField = tweetBodyField;
  }
}
