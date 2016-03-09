package com.sos.jade.backgroundservice.view;

import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.JADE_BS_OPTIONS;
import static com.sos.jade.backgroundservice.JADEHistoryViewerUI.parentNodeName;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sos.jadehistory.JadeFilesHistoryFilter;
import sos.jadehistory.db.JadeFilesHistoryDBItem;

import com.sos.jade.backgroundservice.JADEHistoryViewerUI;
import com.sos.jade.backgroundservice.constants.JadeBSConstants;
import com.sos.jade.backgroundservice.data.JadeDetailsContainer;
import com.sos.jade.backgroundservice.enums.JadeCountry;
import com.sos.jade.backgroundservice.listeners.IJadeFileListener;
import com.sos.jade.backgroundservice.listeners.impl.JadeFileListenerProxy;
import com.sos.jade.backgroundservice.util.JadeBSMessages;
import com.sos.jade.backgroundservice.view.components.JadeDetailTable;
import com.sos.jade.backgroundservice.view.components.JadeFileHistoryTable;
import com.sos.jade.backgroundservice.view.components.JadeMenuBar;
import com.sos.jade.backgroundservice.view.components.filter.DuplicatesFilter;
import com.sos.jade.backgroundservice.view.components.filter.JadeFilesHistoryFilterLayout;
import com.vaadin.data.Item;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.LayoutEvents.LayoutClickEvent;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class MainView extends CustomComponent implements View {

    private static final long serialVersionUID = 6368275374953898482L;
    private static final Long WAIT = 60000L;
    private static final Logger LOGGER = LoggerFactory.getLogger(MainView.class);
    public static final String NAME = "";
    private List<JadeFilesHistoryDBItem> historyItems;
    private JadeFileHistoryTable tblFileHistory;
    private JadeDetailTable tblDetails;
    private Item markedRow;
    private IJadeFileListener fileListener;
    private boolean first = true;
    private CssLayout lblDE;
    private CssLayout lblUK;
    private CssLayout lblUS;
    private CssLayout lblES;
    private JadeMenuBar jmb;
    private Label lblTitle;
    private VerticalLayout vRest;
    private Preferences prefs = JADE_BS_OPTIONS.getPreferenceStore();
    private ProgressBar progress;
    private JadeBSMessages messages;
    private HorizontalLayout hlResetAndProgress;
    private SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");
    private Locale currentLocale = VaadinSession.getCurrent().getLocale();
    private DuplicatesFilter duplicatesFilter = new DuplicatesFilter();
    private Float lastSplitPosition;
    private HorizontalSplitPanel splitter;
    private List<CssLayout> allLangs = new ArrayList<CssLayout>();
    private List<Locale> allLocales = new ArrayList<Locale>();
    private boolean autoRefresh = false;
    private Label lblEntryCount;

    public MainView() {
        this.messages = new JadeBSMessages("JADEBSMessages", currentLocale);
        this.fileListener = new JadeFileListenerProxy(this);
        setSizeFull();
        setImmediate(true);
        initView();
        LOGGER.debug("****************************MainView started!******************************");
    }

    public void initView() {
        initComponents();
        progress.setVisible(true);
        if (checkReuseLastFilterSettings()) {
            runFilter(createReusedFilter());
        } else {
            runFilter(null);
        }
    }

    private void runFilter(final JadeFilesHistoryFilter filter) {
        new FilterThread(filter, true).start();
    }

    private void initComponents() {
        final VerticalLayout vLayout = new VerticalLayout();
        vLayout.setMargin(true);
        vLayout.setSpacing(true);
        vLayout.setSizeFull();
        vLayout.addStyleName("jadeMainVLayout");
        setCompositionRoot(vLayout);
        vLayout.addComponent(createTitleLayout());
        HorizontalLayout hlMenuBar = initHLayout(25.0f);
        hlMenuBar.addStyleName("jadeMenuBarLayout");
        vLayout.addComponent(hlMenuBar);
        jmb = new JadeMenuBar(messages);
        hlMenuBar.addComponent(jmb);
        lblDE = initCountryLabel(JadeCountry.GERMANY);
        lblUK = initCountryLabel(JadeCountry.UK);
        lblUS = initCountryLabel(JadeCountry.US);
        lblES = initCountryLabel(JadeCountry.SPAIN);
        allLangs.add(lblDE);
        allLangs.add(lblUK);
        allLangs.add(lblUS);
        allLangs.add(lblES);
        hlMenuBar.addComponents(lblDE, lblUS, lblUK, lblES);
        hlMenuBar.setExpandRatio(jmb, 1);
        hlMenuBar.setComponentAlignment(lblDE, Alignment.BOTTOM_RIGHT);
        hlMenuBar.setComponentAlignment(lblUS, Alignment.BOTTOM_RIGHT);
        hlMenuBar.setComponentAlignment(lblUK, Alignment.BOTTOM_RIGHT);
        hlMenuBar.setComponentAlignment(lblES, Alignment.BOTTOM_RIGHT);
        setLanguageIconClickHandlers();
        vRest = new VerticalLayout();
        vRest.setSizeFull();
        vLayout.addComponent(vRest);
        vLayout.setExpandRatio(vRest, 1);
        createResetAndProgressLayout();
        createTablesLayout();
        vRest.setExpandRatio(splitter, 1);
    }

    private ProgressBar initProgressBar() {
        ProgressBar progressBar = new ProgressBar();
        progressBar.setImmediate(true);
        progressBar.setIndeterminate(true);
        return progressBar;
    }

    private HorizontalSplitPanel initHSplitPanel() {
        HorizontalSplitPanel split = new HorizontalSplitPanel();
        split.setSizeFull();
        split.setSplitPosition(100.0f);
        return split;
    }

    private HorizontalLayout createTitleLayout() {
        HorizontalLayout hLayout = initHLayout(75.0f);
        final Image imgTitle = new Image();
        ThemeResource titleResource = new ThemeResource("images/job_scheduler_rabbit_circle_60x60.gif");
        imgTitle.setSource(titleResource);
        hLayout.addComponent(imgTitle);
        lblTitle = new Label(messages.getValue("MainView.title", currentLocale));
        lblTitle.setStyleName("jadeTitleLabel");
        hLayout.addComponent(lblTitle);
        final Image imgLogo = new Image();
        ThemeResource trLogo = new ThemeResource("images/sos_logo_84x60_transparent.png");
        imgLogo.setSource(trLogo);
        hLayout.addComponent(imgLogo);
        hLayout.setComponentAlignment(imgLogo, Alignment.MIDDLE_RIGHT);
        hLayout.setExpandRatio(imgTitle, 1);
        hLayout.setExpandRatio(lblTitle, 8);
        hLayout.setExpandRatio(imgLogo, 1);
        return hLayout;
    }

    private HorizontalLayout initHLayout(Float height) {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("100%");
        hl.setHeight(height, Unit.PIXELS);
        return hl;
    }

    private CssLayout initCountryLabel(JadeCountry country) {
        CssLayout layout = new CssLayout();
        Label lbl = new Label();
        lbl.setSizeUndefined();
        switch (country) {
        case GERMANY:
            lbl.setPrimaryStyleName("jadeGermanyLabel");
            layout.setStyleName("jadeGermanyLabel");
            allLocales.add(Locale.GERMANY);
            break;
        case UK:
            lbl.setPrimaryStyleName("jadeUKLabel");
            layout.setStyleName("jadeUKLabel");
            allLocales.add(Locale.UK);
            break;
        case US:
            lbl.setPrimaryStyleName("jadeUSLabel");
            layout.setStyleName("jadeUSLabel");
            allLocales.add(Locale.US);
            break;
        case SPAIN:
            lbl.setPrimaryStyleName("jadeSpainLabel");
            layout.setStyleName("jadeSpainLabel");
            allLocales.add(new Locale("es", "ES"));
            break;
        }
        layout.addComponent(lbl);
        return layout;
    }

    private void disableCurrentLocaleIcon() {
        if (currentLocale != null) {
            String country = currentLocale.getCountry();
            switch (country) {
            case "DE":
                lblDE.setEnabled(false);
                break;
            case "GB":
                lblUK.setEnabled(false);
                break;
            case "US":
                lblUS.setEnabled(false);
                break;
            case "ES":
                lblES.setEnabled(false);
                break;
            }
        }
    }

    private void disableLanguageIconFor(CssLayout lang) {
        for (CssLayout language : allLangs) {
            if (language.equals(lang)) {
                language.setEnabled(false);
            } else {
                language.setEnabled(true);
            }
        }
    }

    private void setLanguageIconClickHandlers() {
        lblDE.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void layoutClick(LayoutClickEvent event) {
                currentLocale = Locale.GERMANY;
                refreshLocalization(currentLocale);
                disableLanguageIconFor(lblDE);
                refreshButtonVisibility();
            }
        });
        lblUK.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void layoutClick(LayoutClickEvent event) {
                currentLocale = Locale.UK;
                refreshLocalization(currentLocale);
                disableLanguageIconFor(lblUK);
                refreshButtonVisibility();
            }
        });
        lblUS.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void layoutClick(LayoutClickEvent event) {
                currentLocale = Locale.US;
                refreshLocalization(currentLocale);
                disableLanguageIconFor(lblUS);
                refreshButtonVisibility();
            }
        });
        lblES.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void layoutClick(LayoutClickEvent event) {
                currentLocale = new Locale("es", "ES");
                refreshLocalization(currentLocale);
                disableLanguageIconFor(lblES);
                refreshButtonVisibility();
            }
        });
    }

    public void refreshLocalization(Locale locale) {
        if (locale == null) {
            locale = Locale.getDefault();
        }
        VaadinSession.getCurrent().setLocale(locale);
        messages.setLocale(locale);
        currentLocale = locale;
        lblTitle.setValue(messages.getValue("MainView.title", locale));
        if (lblEntryCount == null) {
            lblEntryCount = new Label();
            lblEntryCount.addStyleName("jadeEntryCountLabel");
        }
        if (historyItems == null || (historyItems != null && historyItems.size() == 0)) {
            lblEntryCount.setValue(messages.getValue("MainView.noEntries", locale));
        } else {
            lblEntryCount.setValue(messages.getValue("MainView.entryCount", locale) + " " + historyItems.size());
        }
        jmb.refreshCaptions(locale);
        tblFileHistory.refreshColumnHeaders(locale);
        tblDetails.refreshColumnHeaders(locale);
        if (tblDetails.getContainerDataSource().getItemIds() != null && !tblDetails.getContainerDataSource().getItemIds().isEmpty()) {
            ((JadeDetailsContainer) tblDetails.getContainerDataSource()).updateLocale(locale);
        }
        ((JadeFilesHistoryFilterLayout) ((JADEHistoryViewerUI) getUI()).getModalWindow().getContent()).refreshCaptions(locale);
        markAsDirtyRecursive();
    }

    public void refreshButtonVisibility() {
        List<Locale> checkedLocales = jmb.getCheckedLanguages();
        for (Locale checkedLocale : checkedLocales) {
            setButtonVisible(checkedLocale);
        }
        for (Locale locale : allLocales) {
            if (!checkedLocales.contains(locale)) {
                setButtonNotVisible(locale);
            }
        }
        if (!checkedLocales.contains(currentLocale)) {
            currentLocale = Locale.getDefault();
            refreshLocalization(currentLocale);
        }
    }

    private void setButtonNotVisible(Locale locale) {
        if (locale.getCountry().equals(Locale.GERMANY.getCountry())) {
            lblDE.setVisible(false);
        } else if (locale.getCountry().equals(Locale.UK.getCountry())) {
            lblUK.setVisible(false);
        } else if (locale.getCountry().equals(Locale.US.getCountry())) {
            lblUS.setVisible(false);
        } else if (locale.getCountry().equals(new Locale("es", "ES").getCountry())) {
            lblES.setVisible(false);
        }
    }

    private void setButtonVisible(Locale locale) {
        if (locale.getCountry().equals(Locale.GERMANY.getCountry())) {
            lblDE.setVisible(true);
        } else if (locale.getCountry().equals(Locale.UK.getCountry())) {
            lblUK.setVisible(true);
        } else if (locale.getCountry().equals(Locale.US.getCountry())) {
            lblUS.setVisible(true);
        } else if (locale.getCountry().equals(new Locale("es", "ES").getCountry())) {
            lblES.setVisible(true);
        }
    }

    private void createResetAndProgressLayout() {
        hlResetAndProgress = new HorizontalLayout();
        hlResetAndProgress.setWidth("100%");
        vRest.addComponent(hlResetAndProgress);
        Button btnResetColumnWith = new Button();
        btnResetColumnWith.setPrimaryStyleName("jadeResizeButton");
        btnResetColumnWith.setSizeUndefined();
        btnResetColumnWith.setIcon(new ThemeResource("images/resize_orange_40x20.png"));
        btnResetColumnWith.setDescription("reset column width to default");
        hlResetAndProgress.addComponent(btnResetColumnWith);
        btnResetColumnWith.addClickListener(new Button.ClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void buttonClick(Button.ClickEvent event) {
                tblFileHistory.resetColumnWidths();
                if (tblDetails.isVisible()) {
                    tblDetails.resetColumnWidths();
                }
            }
        });
        progress = initProgressBar();
        hlResetAndProgress.addComponent(progress);
        hlResetAndProgress.setComponentAlignment(progress, Alignment.MIDDLE_CENTER);
        hlResetAndProgress.setExpandRatio(progress, 1);
        progress.setVisible(false);
        lblEntryCount = new Label();
        lblEntryCount.addStyleName("jadeEntryCountLabel");
        if (historyItems == null || (historyItems != null && historyItems.size() == 0)) {
            lblEntryCount.setValue(messages.getValue("MainView.noEntries", currentLocale));
        } else {
            lblEntryCount.setValue(messages.getValue("MainView.entryCount", currentLocale) + " " + historyItems.size());
        }
        hlResetAndProgress.addComponent(lblEntryCount);
        hlResetAndProgress.setComponentAlignment(lblEntryCount, Alignment.MIDDLE_CENTER);
    }

    private void createTablesLayout() {
        splitter = initHSplitPanel();
        vRest.addComponent(splitter);
        vRest.setExpandRatio(splitter, 1);
        tblFileHistory = new JadeFileHistoryTable(historyItems, messages);
        splitter.addComponent(tblFileHistory);
        tblFileHistory.addItemClickListener(new ItemClickListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void itemClick(ItemClickEvent event) {
                if (markedRow == null || !markedRow.equals(event.getItemId())) {
                    JadeFilesHistoryDBItem historyItem = (JadeFilesHistoryDBItem) event.getItemId();
                    tblDetails.populateDatasource(historyItem);
                }
                toggleTableVisiblity(event.getItem());
                jmb.filterDetailItems();
            }
        });
        tblFileHistory.addValueChangeListener(new ValueChangeListener() {

            private static final long serialVersionUID = 1L;

            @Override
            public void valueChange(ValueChangeEvent event) {
                progress.setPrimaryStyleName("jadeProgressBar");
                tblFileHistory.refreshRowCache();
                tblFileHistory.markAsDirty();
                progress.setVisible(false);
            }
        });
        tblDetails = new JadeDetailTable(null, messages);
        tblDetails.setVisible(false);
        splitter.addComponent(tblDetails);
    }

    private void setSplitPosition(Boolean itemVisible) {
        Float newLastSplitPosition = splitter.getSplitPosition();
        if (first) {
            first = false;
            splitter.setSplitPosition(75);
        } else if (itemVisible) {
            if (lastSplitPosition.equals(100.0f)) {
                splitter.setSplitPosition(75);
            } else {
                splitter.setSplitPosition(lastSplitPosition);
            }
        } else {
            splitter.setSplitPosition(lastSplitPosition);
        }
        lastSplitPosition = newLastSplitPosition;
    }

    public void setSplitPosition(final float position) {
        lastSplitPosition = splitter.getSplitPosition();
        splitter.setSplitPosition(position);
    }

    public void toggleTableVisiblity(Item item) {
        if (item == null || (markedRow != null && markedRow.equals(item))) {
            markedRow = null;
            tblDetails.setVisible(false);
            setSplitPosition(100.0f);
        } else if (markedRow != null && !markedRow.equals(item)) {
            markedRow = item;
            tblDetails.setVisible(true);
            setSplitPosition(true);
        } else {
            markedRow = item;
            tblDetails.setVisible(true);
            setSplitPosition(true);
        }
    }

    public class AutoRefreshThread extends Thread {

        Date actual = null;
        Date started = new Date();

        @Override
        public void run() {
            try {
                sleep(WAIT);
            } catch (InterruptedException e) {
                LOGGER.error("", e);
            }
            UI.getCurrent().access(new Runnable() {

                @Override
                public void run() {
                    if (!autoRefresh) {
                        return;
                    } else {
                        autoRefresh();
                    }
                }
            });
        }
    }

    private class FilterThread extends Thread {

        private JadeFilesHistoryFilter filter;
        private boolean updateMenuBar;

        public FilterThread(JadeFilesHistoryFilter filter, boolean updateMenuBar) {
            this.filter = filter;
            this.updateMenuBar = updateMenuBar;
        }

        @Override
        public void run() {
            try {
                fileListener.filterJadeFilesHistory(filter);
            } catch (final Exception e) {
                fileListener.logException(e);
            }
            UI.getCurrent().access(new Runnable() {

                @Override
                public void run() {
                    if (updateMenuBar) {
                        jmb.getSmDuplicatesFilter().setChecked(checkRemoveDuplicatesSettings());
                        jmb.getSmPreferencesReuseFilter().setChecked(checkReuseLastFilterSettings());
                    }
                    tblFileHistory.populateDatasource(historyItems);
                    fileListener.closeJadeFilesHistoryDbSession();
                    if (checkRemoveDuplicatesSettings()) {
                        duplicatesFilter.setHistoryItems(historyItems);
                        ((IndexedContainer) tblFileHistory.getContainerDataSource()).addContainerFilter(duplicatesFilter);
                    } else if (((IndexedContainer) tblFileHistory.getContainerDataSource()).hasContainerFilters()) {
                        duplicatesFilter.setHistoryItems(historyItems);
                        ((IndexedContainer) tblFileHistory.getContainerDataSource()).removeContainerFilter(duplicatesFilter);
                    }
                    LOGGER.debug("feedback received from proxy about Hibernate SESSION close at {}!", sdf.format(new Date()));
                }
            });
            UI.getCurrent().access(new Runnable() {

                @Override
                public void run() {
                    if (updateMenuBar) {
                        jmb.refreshSelectedLangOnInit();
                        jmb.refreshSelectedDetailsOnInit();
                        jmb.refreshAutoRefreshOnInit();
                    }
                    markAsDirty();
                }
            });
            UI.getCurrent().access(new Runnable() {

                @Override
                public void run() {
                    if (lblEntryCount == null) {
                        lblEntryCount = new Label();
                        lblEntryCount.addStyleName("jadeEntryCountLabel");
                    }
                    if (historyItems == null || (historyItems != null && historyItems.size() == 0)) {
                        lblEntryCount.setValue(messages.getValue("MainView.noEntries", currentLocale));
                    } else {
                        lblEntryCount.setValue(messages.getValue("MainView.entryCount", currentLocale) + " " + historyItems.size());
                    }
                    refreshButtonVisibility();
                    disableCurrentLocaleIcon();
                    markAsDirty();
                }
            });
        };
    }

    private void autoRefresh() {
        toggleTableVisiblity(null);
        FilterThread filterThread;
        if (checkReuseLastFilterSettings()) {
            filterThread = new FilterThread(createReusedFilter(), false);
        } else {
            filterThread = new FilterThread(null, false);
        }
        filterThread.start();
        do {
            continue;
        } while (filterThread.isAlive());
        new AutoRefreshThread().start();
    }

    private boolean checkReuseLastFilterSettings() {
        boolean lastUsed = false;
        try {
            if (prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).nodeExists(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL)) {
                lastUsed = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL).getBoolean(JadeBSConstants.PREF_KEY_LAST_USED_FILTER, false);
            }
        } catch (BackingStoreException e) {
            LOGGER.warn("Unable to read from PreferenceStore, using defaults.");
        }
        return lastUsed;
    }

    private boolean checkRemoveDuplicatesSettings() {
        boolean removeDuplicates = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_PREFERENCES_GENERAL).getBoolean(JadeBSConstants.PREF_KEY_REMOVE_DUPLICATES, false);
        return removeDuplicates;
    }

    private JadeFilesHistoryFilter createReusedFilter() {
        JadeFilesHistoryFilter filter = new JadeFilesHistoryFilter();
        Long timeFrom = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER).getLong(JadeBSConstants.FILTER_OPTION_TRANSFER_START_FROM, 0L);
        if (timeFrom != 0L) {
            filter.setTransferStartFrom(new Date(timeFrom));
        }
        Long timeTo = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER).getLong(JadeBSConstants.FILTER_OPTION_TRANSFER_START_TO, 0L);
        if (timeTo != 0L) {
            filter.setTransferStartTo(new Date(timeTo));
        }
        String protocol = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER).get(JadeBSConstants.FILTER_OPTION_PROTOCOL, null);
        if (protocol != null && !"".equals(protocol)) {
            filter.setProtocol(protocol);
        }
        String status = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER).get(JadeBSConstants.FILTER_OPTION_STATUS, null);
        if (status != null && !"".equals(status)) {
            filter.setStatus(status);
        }
        String operation = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER).get(JadeBSConstants.FILTER_OPTION_OPERATION, null);
        if (operation != null && !"".equals(operation)) {
            filter.setOperation(operation);
        }
        String sourceFile = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER).get(JadeBSConstants.FILTER_OPTION_SOURCE_FILE, null);
        if (sourceFile != null && !"".equals(sourceFile)) {
            filter.setSourceFile(sourceFile);
        }
        String sourceHost = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER).get(JadeBSConstants.FILTER_OPTION_SOURCE_HOST, null);
        if (sourceHost != null && !"".equals(sourceHost)) {
            filter.setSourceHost(sourceHost);
        }
        String targetFile = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER).get(JadeBSConstants.FILTER_OPTION_TARGET_FILE, null);
        if (targetFile != null && !"".equals(targetFile)) {
            filter.setTargetFilename(targetFile);
        }
        String targetHost = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER).get(JadeBSConstants.FILTER_OPTION_TARGET_HOST, null);
        if (targetHost != null && !"".equals(targetHost)) {
            filter.setTargetHost(targetHost);
        }
        String mandator = prefs.node(parentNodeName).node(JadeBSConstants.PRIMARY_NODE_FILTER).node(JadeBSConstants.PREF_NODE_LAST_USED_FILTER).get(JadeBSConstants.FILTER_OPTION_MANDATOR, null);
        if (mandator != null && !"".equals(mandator)) {
            filter.setMandator(mandator);
        }
        return filter;
    }

    public void setMarkedRow(Item markedRow) {
        this.markedRow = markedRow;
    }

    public void setDetailViewVisible(boolean visible) {
        tblDetails.setVisible(visible);
    }

    public List<JadeFilesHistoryDBItem> getHistoryItems() {
        return historyItems;
    }

    public void setHistoryItems(List<JadeFilesHistoryDBItem> historyItems) {
        this.historyItems = historyItems;
    }

    public JadeFileHistoryTable getTblFileHistory() {
        return tblFileHistory;
    }

    public JadeDetailTable getTblDetails() {
        return tblDetails;
    }

    public JadeBSMessages getMessages() {
        return messages;
    }

    public ProgressBar getProgress() {
        return progress;
    }

    public JadeMenuBar getJmb() {
        return jmb;
    }

    public CssLayout getLblDE() {
        return lblDE;
    }

    public CssLayout getLblUK() {
        return lblUK;
    }

    public CssLayout getLblUS() {
        return lblUS;
    }

    public CssLayout getLblES() {
        return lblES;
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }

    public boolean isAutoRefresh() {
        return autoRefresh;
    }

    public void setAutoRefresh(boolean autoRefresh) {
        this.autoRefresh = autoRefresh;
        if (this.autoRefresh) {
            new AutoRefreshThread().start();
        }
    }

    public Label getLblEntryCount() {
        return lblEntryCount;
    }

    public void setLblEntryCount(Label lblEntryCount) {
        this.lblEntryCount = lblEntryCount;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        String username = String.valueOf(getSession().getAttribute("user"));
        Notification note = new Notification("Login", Type.HUMANIZED_MESSAGE);
        note.show(username + " is logged in");
    }

}
