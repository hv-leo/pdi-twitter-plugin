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

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.*;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.Props;
import org.pentaho.di.core.exception.KettleStepException;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDialogInterface;
import org.pentaho.di.ui.core.FormDataBuilder;
import org.pentaho.di.ui.trans.step.BaseStepDialog;
import twitter4j.Query;
import twitter4j.Query.ResultType;
import java.util.Arrays;

public class TwitterSearchDialog extends BaseStepDialog implements StepDialogInterface {

  private static Class<?> PKG = TwitterSearchMeta.class; // for i18n purposes, needed by Translator2!!   $NON-NLS-1$

  private static final int MARGIN_SIZE = 15;
  private static final int ELEMENT_SPACING = Const.MARGIN;

  private TwitterSearchMeta meta;

  private CTabFolder wTabFolder;
  private CTabItem wQueryTab, wAuthTab, wOutputTab;
  private Composite wQueryComp, wAuthComp, wOutputComp;

  private ScrolledComposite scrolledComposite;
  private Composite contentComposite;

  // Step name.
  private Label wStepNameLabel;
  private Text wStepNameField;

  // Search Query.
  private Label wQueryLabel;
  private CCombo wQueryField;

  // Result Type.
  private Label wTypeLabel;
  private CCombo wTypeField;

  // Language.
  private Label wLangLabel;
  private CCombo wLangField;

  // Sensitive Tweets.
  private Label wSensitiveLabel;
  private Button wSensitiveField;

  // Non-Verified Users.
  private Label wOnlyVerifiedUsersLabel;
  private Button wOnlyVerifiedUsersField;

  // Twitter API - Consumer Key.
  private Label wConsumerKeyLabel;
  private Text wConsumerKeyField;

  // Twitter API - Consumer Secret.
  private Label wConsumerSecretLabel;
  private Text wConsumerSecretField;

  // Twitter API - Access Token Key.
  private Label wAccessTokenKeyLabel;
  private Text wAccessTokenKeyField;

  // Twitter API - Access Token Secret.
  private Label wAccessTokenSecretLabel;
  private Text wAccessTokenSecretField;

  // Tweet Date.
  private Label wDateLabel;
  private Text wDateField;

  // Author - Name.
  private Label wAuthorLabel;
  private Text wAuthorField;

  // Author - Screen Name.
  private Label wAuthorScreenNameLabel;
  private Text wAuthorScreenNameField;

  // Author - Location.
  private Label wLocationLabel;
  private Text wLocationField;

  // Tweet Body.
  private Label wTweetLabel;
  private Text wTweetField;

  // Footer Buttons
  private Button wCancel;
  private Button wOK;

  // Listeners
  private ModifyListener lsMod;
  private Listener lsCancel;
  private Listener lsOK;
  private SelectionAdapter lsDef;
  private boolean changed;

  public TwitterSearchDialog( Shell parent, Object in, TransMeta tr, String sname ) {
    super( parent, (BaseStepMeta) in, tr, sname );
    meta = (TwitterSearchMeta) in;
  }

  public String open() {
    // Set up window
    Shell parent = getParent();
    Display display = parent.getDisplay();

    shell = new Shell( parent, SWT.DIALOG_TRIM | SWT.RESIZE | SWT.MIN | SWT.MAX );
    props.setLook( shell );
    setShellImage( shell, meta );
    int middle = props.getMiddlePct();

    lsMod = new ModifyListener() {
      public void modifyText( ModifyEvent e ) {
        meta.setChanged();
      }
    };
    changed = meta.hasChanged();

    // 15 pixel margins
    FormLayout formLayout = new FormLayout();
    formLayout.marginLeft = MARGIN_SIZE;
    formLayout.marginHeight = MARGIN_SIZE;
    shell.setLayout( formLayout );
    shell.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.Shell.Title" ) );

    // Build a scrolling composite and a composite for holding all content
    scrolledComposite = new ScrolledComposite( shell, SWT.V_SCROLL );
    contentComposite = new Composite( scrolledComposite, SWT.NONE );
    FormLayout contentLayout = new FormLayout();
    contentLayout.marginRight = MARGIN_SIZE;
    contentComposite.setLayout( contentLayout );
    FormData compositeLayoutData = new FormDataBuilder().fullSize()
      .result();
    contentComposite.setLayoutData( compositeLayoutData );
    props.setLook( contentComposite );

    // Step name label and text field.
    wStepNameLabel = new Label( contentComposite, SWT.RIGHT );
    wStepNameLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.Stepname.Label" ) );
    props.setLook( wStepNameLabel );
    FormData fdStepNameLabel = new FormDataBuilder().left()
      .top()
      .right( middle, -ELEMENT_SPACING )
      .result();
    wStepNameLabel.setLayoutData( fdStepNameLabel );

    wStepNameField = new Text( contentComposite, SWT.SINGLE | SWT.LEFT | SWT.BORDER );
    wStepNameField.setText( stepname );
    props.setLook( wStepNameField );
    wStepNameField.addModifyListener( lsMod );
    FormData fdStepName = new FormDataBuilder().left( middle, 0 )
      .top( )
      .right( 100, 0 )
      .result();
    wStepNameField.setLayoutData( fdStepName );

    // Folder that holds all tabs.
    wTabFolder = new CTabFolder( contentComposite, SWT.BORDER );
    props.setLook( wTabFolder, Props.WIDGET_STYLE_TAB );
    FormData fdTabFolder = new FormDataBuilder().left()
      .top( wStepNameField, ELEMENT_SPACING )
      .right( 100, 0 )
      .bottom( 100, -ELEMENT_SPACING )
      .result();
    wTabFolder.setLayoutData( fdTabFolder );

    // Start of Query Tab.
    wQueryTab = new CTabItem( wTabFolder, SWT.NONE );
    wQueryTab.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.Query.Label" ) );
    wQueryComp = new Composite( wTabFolder, SWT.NONE );
    props.setLook( wQueryComp );

    FormLayout tabLayout = new FormLayout();
    tabLayout.marginWidth = ELEMENT_SPACING;
    tabLayout.marginHeight = ELEMENT_SPACING;
    wQueryComp.setLayout( tabLayout );

    // Search Query label/field
    wQueryLabel = new Label( wQueryComp, SWT.RIGHT );
    props.setLook( wQueryLabel );
    wQueryLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.SearchQuery.Label" ) );
    FormData fdlTransformation5 = new FormDataBuilder().left()
      .top( wQueryComp, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wQueryLabel.setLayoutData( fdlTransformation5 );

    wQueryField = new CCombo( wQueryComp, SWT.BORDER );
    props.setLook( wQueryField );
    wQueryField.addModifyListener( lsMod );
    FormData fdTransformation5 = new FormDataBuilder().left( middle, 0 )
      .top( wQueryComp, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wQueryField.setLayoutData( fdTransformation5 );

    // Result Type label/field
    wTypeLabel = new Label( wQueryComp, SWT.RIGHT );
    props.setLook( wTypeLabel );
    wTypeLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.ResultType.Label" ) );
    FormData fdlTransformation6 = new FormDataBuilder().left()
      .top( wQueryField, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wTypeLabel.setLayoutData( fdlTransformation6 );

    wTypeField = new CCombo( wQueryComp, SWT.BORDER );
    props.setLook( wTypeField );
    wTypeField.addModifyListener( lsMod );
    FormData fdTransformation6 = new FormDataBuilder().left( middle, 0 )
      .top( wQueryField, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wTypeField.setLayoutData( fdTransformation6 );

    // Tweet Language label/field
    wLangLabel = new Label( wQueryComp, SWT.RIGHT );
    props.setLook( wLangLabel );
    wLangLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.Lang.Label" ) );
    FormData fdlTransformation7 = new FormDataBuilder().left()
      .top( wTypeField, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wLangLabel.setLayoutData( fdlTransformation7 );

    wLangField = new CCombo( wQueryComp, SWT.BORDER );
    props.setLook( wLangField );
    wLangField.addModifyListener( lsMod );
    FormData fdTransformation7 = new FormDataBuilder().left( middle, 0 )
      .top( wTypeField, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wLangField.setLayoutData( fdTransformation7 );

    // Sensitive Checkbox label/field
    wSensitiveLabel = new Label( wQueryComp, SWT.RIGHT );
    props.setLook( wLangLabel );
    wSensitiveLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.Sensitive.Label" ) );
    FormData fdlTransformation8 = new FormDataBuilder().left()
      .top( wLangField, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wSensitiveLabel.setLayoutData( fdlTransformation8 );

    wSensitiveField = new Button( wQueryComp, SWT.CHECK );
    props.setLook( wSensitiveField );
    SelectionAdapter lsSelMod = new SelectionAdapter() {
      public void widgetSelected( SelectionEvent arg0 ) {
        meta.setChanged();
      }
    };
    wSensitiveField.addSelectionListener( lsSelMod );
    FormData fdTransformation8 = new FormDataBuilder().left( middle, 0 )
      .top( wLangField, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wSensitiveField.setLayoutData( fdTransformation8 );

    // Non-Verified Users label/field
    wOnlyVerifiedUsersLabel = new Label( wQueryComp, SWT.RIGHT );
    props.setLook( wOnlyVerifiedUsersLabel );
    wOnlyVerifiedUsersLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.NonVerifiedUsers.Label" ) );
    FormData fdlTransformation14 = new FormDataBuilder().left()
      .top( wSensitiveField, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wOnlyVerifiedUsersLabel.setLayoutData( fdlTransformation14 );

    wOnlyVerifiedUsersField = new Button( wQueryComp, SWT.CHECK );
    props.setLook( wOnlyVerifiedUsersField );
    wOnlyVerifiedUsersField.addSelectionListener( lsSelMod );
    FormData fdTransformation14 = new FormDataBuilder().left( middle, 0 )
      .top( wSensitiveField, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wOnlyVerifiedUsersField.setLayoutData( fdTransformation14 );

    wQueryComp.layout();
    wQueryTab.setControl( wQueryComp );

    // Start of Authentication Tab.
    wAuthTab = new CTabItem( wTabFolder, SWT.NONE );
    wAuthTab.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.Authentication.Label" ) );
    wAuthComp = new Composite( wTabFolder, SWT.NONE );
    props.setLook( wAuthComp );

    tabLayout = new FormLayout();
    tabLayout.marginWidth = ELEMENT_SPACING;
    tabLayout.marginHeight = ELEMENT_SPACING;
    wAuthComp.setLayout( tabLayout );

    // Consumer Key label/field
    wConsumerKeyLabel = new Label( wAuthComp, SWT.RIGHT );
    props.setLook( wConsumerKeyLabel );
    wConsumerKeyLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.ConsumerKey.Label" ) );
    FormData fdlTransformation = new FormDataBuilder().left()
      .top()
      .right( middle, -ELEMENT_SPACING )
      .result();
    wConsumerKeyLabel.setLayoutData( fdlTransformation );

    wConsumerKeyField = new Text( wAuthComp, SWT.BORDER );
    props.setLook( wConsumerKeyField );
    wConsumerKeyField.addModifyListener( lsMod );
    FormData fdTransformation = new FormDataBuilder().left( middle, 0 )
      .top()
      .right( 100, 0 )
      .result();
    wConsumerKeyField.setLayoutData( fdTransformation );

    // Consumer Secret label/field
    wConsumerSecretLabel = new Label( wAuthComp, SWT.RIGHT );
    props.setLook( wConsumerSecretLabel );
    wConsumerSecretLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.ConsumerSecret.Label" ) );
    FormData fdlTransformation2 = new FormDataBuilder().left()
      .top( wConsumerKeyField, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wConsumerSecretLabel.setLayoutData( fdlTransformation2 );

    wConsumerSecretField = new Text( wAuthComp, SWT.BORDER );
    props.setLook( wConsumerSecretField );
    wConsumerSecretField.addModifyListener( lsMod );
    FormData fdTransformation2 = new FormDataBuilder().left( middle, 0 )
      .top( wConsumerKeyField, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wConsumerSecretField.setLayoutData( fdTransformation2 );

    // Access Token Key label/field
    wAccessTokenKeyLabel = new Label( wAuthComp, SWT.RIGHT );
    props.setLook( wAccessTokenKeyLabel );
    wAccessTokenKeyLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.AccessTokenKey.Label" ) );
    FormData fdlTransformation3 = new FormDataBuilder().left()
      .top( wConsumerSecretField, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wAccessTokenKeyLabel.setLayoutData( fdlTransformation3 );

    wAccessTokenKeyField = new Text( wAuthComp, SWT.BORDER );
    props.setLook( wAccessTokenKeyField );
    wAccessTokenKeyField.addModifyListener( lsMod );
    FormData fdTransformation3 = new FormDataBuilder().left( middle, 0 )
      .top( wConsumerSecretField, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wAccessTokenKeyField.setLayoutData( fdTransformation3 );

    // Access Token Secret label/field
    wAccessTokenSecretLabel = new Label( wAuthComp, SWT.RIGHT );
    props.setLook( wAccessTokenSecretLabel );
    wAccessTokenSecretLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.AccessTokenSecret.Label" ) );
    FormData fdlTransformation4 = new FormDataBuilder().left()
      .top( wAccessTokenKeyField, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wAccessTokenSecretLabel.setLayoutData( fdlTransformation4 );

    wAccessTokenSecretField = new Text( wAuthComp, SWT.BORDER );
    props.setLook( wAccessTokenSecretField );
    wAccessTokenSecretField.addModifyListener( lsMod );
    FormData fdTransformation4 = new FormDataBuilder().left( middle, 0 )
      .top( wAccessTokenKeyField, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wAccessTokenSecretField.setLayoutData( fdTransformation4 );

    wAuthComp.layout();
    wAuthTab.setControl( wAuthComp );

    // Start of Authentication Tab.
    wOutputTab = new CTabItem( wTabFolder, SWT.NONE );
    wOutputTab.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.Output.Label" ) );
    wOutputComp = new Composite( wTabFolder, SWT.NONE );
    props.setLook( wAuthComp );

    tabLayout = new FormLayout();
    tabLayout.marginWidth = ELEMENT_SPACING;
    tabLayout.marginHeight = ELEMENT_SPACING;
    wOutputComp.setLayout( tabLayout );

    // Tweet Date label/field
    wDateLabel = new Label( wOutputComp, SWT.RIGHT );
    props.setLook( wDateLabel );
    wDateLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.TweetDate.Label" ) );
    FormData fdlTransformation9 = new FormDataBuilder().left()
      .top( wOutputComp, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wDateLabel.setLayoutData( fdlTransformation9 );

    wDateField = new Text( wOutputComp, SWT.BORDER );
    props.setLook( wDateField );
    wDateField.addModifyListener( lsMod );
    FormData fdTransformation9 = new FormDataBuilder().left( middle, 0 )
      .top( wOutputComp, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wDateField.setLayoutData( fdTransformation9 );

    // Tweet Author label/field
    wAuthorLabel = new Label( wOutputComp, SWT.RIGHT );
    props.setLook( wAuthorLabel );
    wAuthorLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.TweetAuthor.Label" ) );
    FormData fdlTransformation10 = new FormDataBuilder().left()
      .top( wDateField, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wAuthorLabel.setLayoutData( fdlTransformation10 );

    wAuthorField = new Text( wOutputComp, SWT.BORDER );
    props.setLook( wAuthorField );
    wAuthorField.addModifyListener( lsMod );
    FormData fdTransformation10 = new FormDataBuilder().left( middle, 0 )
      .top( wDateField, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wAuthorField.setLayoutData( fdTransformation10 );

    // Author Screen Name label/field
    wAuthorScreenNameLabel = new Label( wOutputComp, SWT.RIGHT );
    props.setLook( wAuthorScreenNameLabel );
    wAuthorScreenNameLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.AuthorScreenName.Label" ) );
    FormData fdlTransformation11 = new FormDataBuilder().left()
      .top( wAuthorField, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wAuthorScreenNameLabel.setLayoutData( fdlTransformation11 );

    wAuthorScreenNameField = new Text( wOutputComp, SWT.BORDER );
    props.setLook( wAuthorScreenNameField );
    wAuthorScreenNameField.addModifyListener( lsMod );
    FormData fdTransformation11 = new FormDataBuilder().left( middle, 0 )
      .top( wAuthorField, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wAuthorScreenNameField.setLayoutData( fdTransformation11 );

    // Author Location label/field
    wLocationLabel = new Label( wOutputComp, SWT.RIGHT );
    props.setLook( wLocationLabel );
    wLocationLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.AuthorLocation.Label" ) );
    FormData fdlTransformation12 = new FormDataBuilder().left()
      .top( wAuthorScreenNameField, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wLocationLabel.setLayoutData( fdlTransformation12 );

    wLocationField = new Text( wOutputComp, SWT.BORDER );
    props.setLook( wLocationField );
    wLocationField.addModifyListener( lsMod );
    FormData fdTransformation12 = new FormDataBuilder().left( middle, 0 )
      .top( wAuthorScreenNameField, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wLocationField.setLayoutData( fdTransformation12 );

    // Tweet Body label/field
    wTweetLabel = new Label( wOutputComp, SWT.RIGHT );
    props.setLook( wTweetLabel );
    wTweetLabel.setText( BaseMessages.getString( PKG, "TwitterSearchDialog.TweetBody.Label" ) );
    FormData fdlTransformation13 = new FormDataBuilder().left()
      .top( wLocationField, ELEMENT_SPACING )
      .right( middle, -ELEMENT_SPACING )
      .result();
    wTweetLabel.setLayoutData( fdlTransformation13 );

    wTweetField = new Text( wOutputComp, SWT.BORDER );
    props.setLook( wTweetField );
    wTweetField.addModifyListener( lsMod );
    FormData fdTransformation13 = new FormDataBuilder().left( middle, 0 )
      .top( wLocationField, ELEMENT_SPACING )
      .right( 100, 0 )
      .result();
    wTweetField.setLayoutData( fdTransformation13 );

    wOutputComp.layout();
    wOutputTab.setControl( wOutputComp );

    // Cancel, action and OK buttons for the bottom of the window.
    wCancel = new Button( shell, SWT.PUSH );
    wCancel.setText( BaseMessages.getString( PKG, "System.Button.Cancel" ) );
    FormData fdCancel = new FormDataBuilder().right( 100, -MARGIN_SIZE )
      .bottom()
      .result();
    wCancel.setLayoutData( fdCancel );

    wOK = new Button( shell, SWT.PUSH );
    wOK.setText( BaseMessages.getString( PKG, "System.Button.OK" ) );
    FormData fdOk = new FormDataBuilder().right( wCancel, -ELEMENT_SPACING )
      .bottom()
      .result();
    wOK.setLayoutData( fdOk );

    // Space between bottom buttons and and group content.
    Label bottomSpacer = new Label( shell, SWT.HORIZONTAL | SWT.SEPARATOR );
    FormData fdhSpacer = new FormDataBuilder().left()
      .right( 100, -MARGIN_SIZE )
      .bottom( wCancel, -MARGIN_SIZE )
      .result();
    bottomSpacer.setLayoutData( fdhSpacer );

    // Add everything to the scrolling composite
    scrolledComposite.setContent( contentComposite );
    scrolledComposite.setExpandVertical( true );
    scrolledComposite.setExpandHorizontal( true );
    scrolledComposite.setMinSize( contentComposite.computeSize( SWT.DEFAULT, SWT.DEFAULT ) );

    scrolledComposite.setLayout( new FormLayout() );
    FormData fdScrolledComposite = new FormDataBuilder().fullWidth()
      .top()
      .bottom( bottomSpacer, -MARGIN_SIZE )
      .result();
    scrolledComposite.setLayoutData( fdScrolledComposite );
    props.setLook( scrolledComposite );

    // Listeners
    lsCancel = new Listener() {
      public void handleEvent( Event e ) {
        cancel();
      }
    };
    lsOK = new Listener() {
      public void handleEvent( Event e ) {
        ok();
      }
    };

    wOK.addListener( SWT.Selection, lsOK );
    wCancel.addListener( SWT.Selection, lsCancel );

    lsDef = new SelectionAdapter() {
      public void widgetDefaultSelected( SelectionEvent e ) {
        ok();
      }
    };
    wStepNameField.addSelectionListener( lsDef );

    shell.addShellListener( new ShellAdapter() {
      public void shellClosed( ShellEvent e ) {
        cancel();
      }
    } );

    // Show shell
    setSize();

    // Populate Window.
    getData();
    meta.setChanged( changed );
    wTabFolder.setSelection( 0 );

    shell.open();
    while ( !shell.isDisposed() ) {
      if ( !display.readAndDispatch() ) {
        display.sleep();
      }
    }
    return stepname;
  }

  /**
   * Copy information from the meta-data input to the dialog fields.
   */
  public void getData() {
    // Add previous fields to search query combo box options.
    try {
      String[] prevFields = transMeta.getPrevStepFields( stepname ).getFieldNames();
      Arrays.stream( prevFields ).forEach( field -> wQueryField.add( field ) );
    } catch ( KettleStepException e ) {
      e.printStackTrace();
    }

    String searchQueryField = meta.getSearchQueryField();
    if ( searchQueryField != null ) {
      wQueryField.setText( searchQueryField );
    }

    ResultType[] types = Query.ResultType.values();
    Arrays.stream( types )
      .forEach( type -> wTypeField.add( type.toString() ) );

    ResultType resultTypeField = meta.getResultTypeField();
    if ( resultTypeField != null ) {
      wTypeField.setText( resultTypeField.toString() );
    }

    // Get Twitter supported languages.
    Arrays.stream( meta.getSupportedLanguages() )
      .forEach( lang -> wLangField.add( lang ) );

    String langField = meta.getLangField();
    if ( langField != null ) {
      wLangField.setText( langField );
    }

    wSensitiveField.setSelection( meta.isSensitiveField() );
    wOnlyVerifiedUsersField.setSelection( meta.isOnlyVerifiedUsersField() );

    String consumerKeyField = meta.getConsumerKeyField();
    if ( consumerKeyField != null ) {
      wConsumerKeyField.setText( consumerKeyField );
    }

    String consumerSecretField = meta.getConsumerSecretField();
    if ( consumerSecretField != null ) {
      wConsumerSecretField.setText( consumerSecretField );
    }

    String accessTokenKeyField = meta.getAccessTokenKeyField();
    if ( accessTokenKeyField != null ) {
      wAccessTokenKeyField.setText( accessTokenKeyField );
    }

    String accessTokenSecretField = meta.getAccessTokenSecretField();
    if ( accessTokenSecretField != null ) {
      wAccessTokenSecretField.setText( accessTokenSecretField );
    }

    String dateField = meta.getCreationDateField();
    if ( dateField != null ) {
      wDateField.setText( dateField );
    }

    String authorField = meta.getAuthorField();
    if ( authorField != null ) {
      wAuthorField.setText( authorField );
    }

    String authorScreenName = meta.getAuthorScreenNameField();
    if ( authorScreenName != null ) {
      wAuthorScreenNameField.setText( authorScreenName );
    }

    String authorLocationField = meta.getAuthorLocationField();
    if ( authorLocationField != null ) {
      wLocationField.setText( authorLocationField );
    }

    String tweetBodyField = meta.getTweetBodyField();
    if ( tweetBodyField != null ) {
      wTweetField.setText( tweetBodyField );
    }
  }

  /**
   * Save information from dialog fields to the meta-data input.
   */
  private void getMeta( TwitterSearchMeta meta ) {
    meta.setSearchQueryField( wQueryField.getText() );
    meta.setResultTypeField( wTypeField.getText() );
    meta.setLangField( wLangField.getText() );
    meta.setSensitiveField( wSensitiveField.getSelection() );
    meta.setOnlyVerifiedUsersField( wOnlyVerifiedUsersField.getSelection() );
    meta.setConsumerKeyField( wConsumerKeyField.getText() );
    meta.setConsumerSecretField( wConsumerSecretField.getText() );
    meta.setAccessTokenKeyField( wAccessTokenKeyField.getText() );
    meta.setAccessTokenSecretField( wAccessTokenSecretField.getText() );
    meta.setCreationDateField( wDateField.getText() );
    meta.setAuthorField( wAuthorField.getText() );
    meta.setAuthorScreenNameField( wAuthorScreenNameField.getText() );
    meta.setAuthorLocationField( wLocationField.getText() );
    meta.setTweetBodyField( wTweetField.getText() );
  }

  private void cancel() {
    meta.setChanged( changed );
    dispose();
  }

  private void ok() {
    getMeta( meta );
    stepname = wStepNameField.getText();
    dispose();
  }
}
