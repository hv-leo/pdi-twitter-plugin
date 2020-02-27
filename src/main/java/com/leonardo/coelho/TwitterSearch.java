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

import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.row.RowDataUtil;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStep;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import twitter4j.Query;
import twitter4j.Query.ResultType;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import java.util.Arrays;

/**
 * Describe your step plugin.
 * 
 */
public class TwitterSearch extends BaseStep implements StepInterface {
  private static Class<?> PKG = TwitterSearchMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$

  private TwitterSearchMeta meta;
  private TwitterSearchData data;
  private Twitter twitter;

  public TwitterSearch( StepMeta stepMeta, StepDataInterface stepDataInterface, int copyNr, TransMeta transMeta,
    Trans trans ) {
    super( stepMeta, stepDataInterface, copyNr, transMeta, trans );
  }

  /**
     * Initialize and do work where other steps need to wait for...
     *
     * @param stepMetaInterface
     *          The metadata to work with
     * @param stepDataInterface
     *          The data to initialize
     */
  public boolean init( StepMetaInterface stepMetaInterface, StepDataInterface stepDataInterface ) {
    meta = (TwitterSearchMeta) stepMetaInterface;
    data = (TwitterSearchData) stepDataInterface;

    if ( super.init( stepMetaInterface, stepDataInterface ) ) {
      if ( Utils.isEmpty( meta.getSearchQueryField() ) ) {
        logError( BaseMessages.getString( PKG, "TwitterSearch.Missing.SearchQuery" ) );
        return false;
      }
      if ( Utils.isEmpty( meta.getConsumerKeyField() ) ) {
        logError( BaseMessages.getString( PKG, "TwitterSearch.Missing.ConsumerKey" ) );
        return false;
      }
      if ( Utils.isEmpty( meta.getConsumerSecretField() ) ) {
        logError( BaseMessages.getString( PKG, "TwitterSearch.Missing.ConsumerSecret" ) );
        return false;
      }
      if ( Utils.isEmpty( meta.getAccessTokenKeyField() ) ) {
        logError( BaseMessages.getString( PKG, "TwitterSearch.Missing.AccessTokenKey" ) );
        return false;
      }
      if ( Utils.isEmpty( meta.getAccessTokenSecretField() ) ) {
        logError( BaseMessages.getString( PKG, "TwitterSearch.Missing.AccessTokenSecret" ) );
        return false;
      }
      return true;
    } else {
      return false;
    }
  }

  public boolean processRow( StepMetaInterface smi, StepDataInterface sdi ) throws KettleException {
    meta = (TwitterSearchMeta) smi;
    data = (TwitterSearchData) sdi;

    Object[] r = getRow(); // get row, set busy!
    if ( r == null ) {
      // no more input to be expected...
      setOutputDone();
      return false;
    }

    if ( first ) {
      first = false;

      data.queryIdx = Arrays.binarySearch( getInputRowMeta().getFieldNames( ), meta.getSearchQueryField() );

      ConfigurationBuilder cb = new ConfigurationBuilder();
      cb.setDebugEnabled( true )
        .setOAuthConsumerKey( meta.getConsumerKeyField() )
        .setOAuthConsumerSecret( meta.getConsumerSecretField() )
        .setOAuthAccessToken( meta.getAccessTokenKeyField() )
        .setOAuthAccessTokenSecret( meta.getAccessTokenSecretField() );
      TwitterFactory tf = new TwitterFactory( cb.build() );
      twitter = tf.getInstance();

      data.outputRowMeta = getInputRowMeta().clone();
      meta.getFields( data.outputRowMeta, getStepname(), null, null, this, repository, metaStore );
      data.startPoint = getInputRowMeta().size();
      r = RowDataUtil.resizeArray( r, data.outputRowMeta.size() );
    }

    Query query = new Query( (String) r[data.queryIdx] );
    query.setResultType( meta.getResultTypeField() );
    query.setLang( meta.getLangCodeField() );
    QueryResult result;
    try {
      result = twitter.search( query );
    } catch ( TwitterException e ) {
      logError( BaseMessages.getString( PKG, "TwitterSearch.Search.Exception", e.getMessage() ) );
      return false;
    }
    for ( Status status : result.getTweets() ) {
      if ( ( !meta.isSensitiveField() || !status.isPossiblySensitive() )
        && ( !meta.isOnlyVerifiedUsersField() || status.getUser().isVerified() ) ) {
        int idx = data.startPoint;
        if ( !Utils.isEmpty( meta.getCreationDateField() ) ) {
          r[ idx++ ] = status.getCreatedAt();
        }
        if ( !Utils.isEmpty( meta.getAuthorField() ) ) {
          r[ idx++ ] =  status.getUser().getName();
        }
        if ( !Utils.isEmpty( meta.getAuthorScreenNameField() ) ) {
          r[ idx++ ] =  status.getUser().getScreenName();
        }
        if ( !Utils.isEmpty( meta.getAuthorLocationField() ) ) {
          r[ idx++ ] = status.getUser().getLocation();
        }
        if ( !Utils.isEmpty( meta.getTweetBodyField() ) ) {
          r[ idx++ ] = status.getText();
        }
        putRow( data.outputRowMeta, r );
      }
    }

    if ( checkFeedback( getLinesRead() ) ) {
      if ( log.isBasic() ) {
        logBasic( BaseMessages.getString( PKG, "TwitterSearch.Log.LineNumber" ) + getLinesRead() );
      }
    }

    return true;
  }
}
