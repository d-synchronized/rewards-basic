/**
 * Copyright 2015, Red Hat, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.examples.web;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.mail.Session;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.examples.util.StartupBean;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jbpm.services.ejb.api.UserTaskServiceEJBLocal;
import org.jbpm.services.task.commands.CompleteTaskCommand;
import org.jbpm.services.task.commands.CompositeCommand;
import org.jbpm.services.task.commands.StartTaskCommand;
import org.kie.api.task.model.TaskSummary;

public class TaskServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @EJB
    private UserTaskServiceEJBLocal userTaskService;

    @EJB
    private RuntimeDataServiceEJBLocal runtimeDataService;

    @Resource(mappedName = "java:jboss/mail/knowledge-helpdesk")
    private Session emailSession;

    @Override
    protected void doGet(final HttpServletRequest req, final HttpServletResponse res) throws ServletException, IOException {

        final String cmd = req.getParameter("cmd");
        final String user = req.getParameter("user");

        if (cmd.equals("list")) {

            List<TaskSummary> taskList;
            try {
                taskList = runtimeDataService.getTasksAssignedAsPotentialOwner(user, null);
            } catch (final Exception e) {
                throw new ServletException(e);
            }
            req.setAttribute("taskList", taskList);
            final ServletContext context = this.getServletContext();
            final RequestDispatcher dispatcher = context.getRequestDispatcher("/task.jsp");
            dispatcher.forward(req, res);

        } else if (cmd.equals("approve")) {

            String message;
            final long taskId = Long.parseLong(req.getParameter("taskId"));
            try {
                final CompositeCommand compositeCommand = new CompositeCommand(new CompleteTaskCommand(taskId, user, null),
                    new StartTaskCommand(taskId, user));
                userTaskService.execute(StartupBean.DEPLOYMENT_ID, compositeCommand);
                message = "Task (id = " + taskId + ") has been completed by " + user;
                System.out.println(message);
                final EmailService emailService = new EmailServiceImpl(emailSession);
                // FIXME
                if (true /** If user is of type manager **/
                ) {
                    final List<String> ccEmails = new ArrayList<String>();
                    ccEmails.add("developer email");
                    ccEmails.add("hr email");
                    emailService.sentEmail("autrivic@gmail.com", "manager@gmail.com", "Approval email", "Approval email", ccEmails);
                } else {
                    final List<String> ccEmails = new ArrayList<String>();
                    ccEmails.add("developer email");
                    ccEmails.add("manager email");
                    emailService.sentEmail("d.synchronized@gmail.com", "dishant.anand@techblue.co.uk", "Approval email", "Approval email", ccEmails);
                }
            } catch (final Exception e) {
                message = "Task operation failed. Please retry : " + e.getMessage();
                throw new ServletException(e);
            }
            req.setAttribute("message", message);
            final ServletContext context = this.getServletContext();
            final RequestDispatcher dispatcher = context.getRequestDispatcher("/index.jsp");
            dispatcher.forward(req, res);

        }
    }

}