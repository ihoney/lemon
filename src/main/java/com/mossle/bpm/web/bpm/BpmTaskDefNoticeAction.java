package com.mossle.bpm.web.bpm;

import java.util.*;
import java.util.ArrayList;
import java.util.List;

import com.mossle.bpm.cmd.FindTaskDefinitionsCmd;
import com.mossle.bpm.persistence.domain.BpmMailTemplate;
import com.mossle.bpm.persistence.domain.BpmProcess;
import com.mossle.bpm.persistence.domain.BpmTaskDefNotice;
import com.mossle.bpm.persistence.manager.BpmMailTemplateManager;
import com.mossle.bpm.persistence.manager.BpmProcessManager;
import com.mossle.bpm.persistence.manager.BpmTaskDefNoticeManager;

import com.mossle.core.export.Exportor;
import com.mossle.core.export.TableModel;
import com.mossle.core.hibernate.PropertyFilter;
import com.mossle.core.mapper.BeanMapper;
import com.mossle.core.page.Page;
import com.mossle.core.struts2.BaseAction;

import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;

import org.activiti.engine.ProcessEngine;
import org.activiti.engine.impl.task.TaskDefinition;
import org.activiti.engine.repository.ProcessDefinition;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;

import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;

@Results(@Result(name = BpmTaskDefNoticeAction.RELOAD, location = "bpm-process!config.do?id=${bpmProcessId}", type = "redirect"))
public class BpmTaskDefNoticeAction extends BaseAction implements
        ModelDriven<BpmTaskDefNotice>, Preparable {
    public static final String RELOAD = "reload";
    private BpmTaskDefNoticeManager bpmTaskDefNoticeManager;
    private BpmProcessManager bpmProcessManager;
    private BpmMailTemplateManager bpmMailTemplateManager;
    private MessageSourceAccessor messages;
    private Page page = new Page();
    private BpmTaskDefNotice model;
    private long id;
    private List<Long> selectedItem = new ArrayList<Long>();
    private Exportor exportor = new Exportor();
    private BeanMapper beanMapper = new BeanMapper();
    private Long bpmCategoryId;
    private List<TaskDefinition> taskDefinitions;
    private Map<TaskDefinition, List<?>> taskMap = new LinkedHashMap<TaskDefinition, List<?>>();
    private ProcessEngine processEngine;
    private List<BpmMailTemplate> bpmMailTemplates;
    private long bpmMailTemplateId;
    private long bpmProcessId;

    public String execute() {
        return list();
    }

    public String list() {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = bpmTaskDefNoticeManager.pagedQuery(page, propertyFilters);

        return SUCCESS;
    }

    public void prepareSave() {
        model = new BpmTaskDefNotice();
    }

    public String save() {
        BpmTaskDefNotice dest = null;

        if (id > 0) {
            dest = bpmTaskDefNoticeManager.get(id);
            beanMapper.copy(model, dest);
        } else {
            dest = model;
        }

        dest.setBpmProcess(bpmProcessManager.get(bpmProcessId));
        dest.setBpmMailTemplate(bpmMailTemplateManager.get(bpmMailTemplateId));
        bpmTaskDefNoticeManager.save(dest);

        addActionMessage(messages.getMessage("core.success.save", "保存成功"));

        return RELOAD;
    }

    public String removeAll() {
        List<BpmTaskDefNotice> bpmCategories = bpmTaskDefNoticeManager
                .findByIds(selectedItem);
        bpmTaskDefNoticeManager.removeAll(bpmCategories);
        addActionMessage(messages.getMessage("core.success.delete", "删除成功"));

        return RELOAD;
    }

    public String input() {
        if (id > 0) {
            model = bpmTaskDefNoticeManager.get(id);
        }

        bpmMailTemplates = bpmMailTemplateManager.getAll();

        return INPUT;
    }

    public void exportExcel() throws Exception {
        List<PropertyFilter> propertyFilters = PropertyFilter
                .buildFromHttpRequest(ServletActionContext.getRequest());
        page = bpmTaskDefNoticeManager.pagedQuery(page, propertyFilters);

        List<BpmTaskDefNotice> bpmCategories = (List<BpmTaskDefNotice>) page
                .getResult();
        TableModel tableModel = new TableModel();
        tableModel.setName("bpm-process");
        tableModel.addHeaders("id", "name");
        tableModel.setData(bpmCategories);
        exportor.exportExcel(ServletActionContext.getResponse(), tableModel);
    }

    public String removeNotice() {
        BpmTaskDefNotice bpmTaskDefNotice = bpmTaskDefNoticeManager.get(id);
        bpmProcessId = bpmTaskDefNotice.getBpmProcess().getId();
        bpmTaskDefNoticeManager.remove(bpmTaskDefNotice);

        return RELOAD;
    }

    // ~ ======================================================================
    public void prepare() {
    }

    public BpmTaskDefNotice getModel() {
        return model;
    }

    public void setBpmTaskDefNoticeManager(
            BpmTaskDefNoticeManager bpmTaskDefNoticeManager) {
        this.bpmTaskDefNoticeManager = bpmTaskDefNoticeManager;
    }

    public void setBpmProcessManager(BpmProcessManager bpmProcessManager) {
        this.bpmProcessManager = bpmProcessManager;
    }

    public void setBpmMailTemplateManager(
            BpmMailTemplateManager bpmMailTemplateManager) {
        this.bpmMailTemplateManager = bpmMailTemplateManager;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    public void setProcessEngine(ProcessEngine processEngine) {
        this.processEngine = processEngine;
    }

    // ~ ======================================================================
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Page getPage() {
        return page;
    }

    public void setSelectedItem(List<Long> selectedItem) {
        this.selectedItem = selectedItem;
    }

    // ~ ======================================================================
    public List<TaskDefinition> getTaskDefinitions() {
        return taskDefinitions;
    }

    public Map<TaskDefinition, List<?>> getTaskMap() {
        return taskMap;
    }

    public List<BpmMailTemplate> getBpmMailTemplates() {
        return bpmMailTemplates;
    }

    public void setBpmMailTemplateId(long bpmMailTemplateId) {
        this.bpmMailTemplateId = bpmMailTemplateId;
    }

    public long getBpmProcessId() {
        return bpmProcessId;
    }

    public void setBpmProcessId(long bpmProcessId) {
        this.bpmProcessId = bpmProcessId;
    }
}
