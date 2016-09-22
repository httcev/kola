<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="de" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>	<html lang="de" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>	<html lang="de" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>	<html lang="de" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="de" class="no-js"><!--<![endif]-->
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
	<title><g:layoutTitle default="KOLA"/></title>
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<link rel="shortcut icon" href="${assetPath(src: 'favicon.png')}" type="image/png">
	<link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
	<link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
	<asset:stylesheet src="application.css"/>
	<asset:javascript src="application.js"/>
	<g:layoutHead/>
</head>
<body class="${controllerName} ${actionName}">
	<sec:ifLoggedIn>
		<div id="sidebar-wrapper">
			<ul class="sidemenu">
				<li class="visible-xs-block"><a href="#" class="toggle-sidemenu" onclick="$('#sidebar-wrapper').toggleClass('expanded'); return false;"><i class="fa fa-fw"></i> <span class="hide-sidemenu-collapsed">Menü zuklappen</span></a></li>
				<li class="${controllerName == 'index' ? 'active' : ''}"><g:link uri="/"><i class="fa fa-fw fa-home"></i> <span class="hide-sidemenu-collapsed"><g:message code="app.home" /></span></g:link></li>
				<li class="${(controllerName == 'task' && !params.isTemplate?.toBoolean()) ? 'active' : ''}"><g:link controller="task" action="index" data-toggle="tooltip" data-placement="right" data-container="body" title="${message(code:'kola.tasks.tooltip')}"><i class="httc-fw httc-task"></i> <span class="hide-sidemenu-collapsed"><g:message code="kola.tasks" /></span></g:link></li>
				<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_TASK_TEMPLATE_CREATOR">
					<li class="${(controllerName == 'task' && params.isTemplate?.toBoolean()) ? 'active' : ''}"><g:link controller="task" action="index" params="[isTemplate:true]" data-toggle="tooltip" data-placement="right" data-container="body" title="${message(code:'kola.taskTemplates.tooltip')}"><i class="fa fa-fw fa-clipboard"></i> <span class="hide-sidemenu-collapsed"><g:message code="kola.taskTemplates" /></span></g:link></li>
				</sec:ifAnyGranted>
                <li class="${controllerName == 'taskDocumentation' ? 'active' : ''}"><g:link controller="taskDocumentation" action="index" data-toggle="tooltip" data-placement="right" data-container="body" title="${message(code:'kola.task.documentations.tooltip')}"><i class="httc-fw httc-compose"></i> <span class="hide-sidemenu-collapsed"><g:message code="kola.task.documentations" /></span></g:link></li>
				<li class="${controllerName == 'asset' ? 'active' : ''}"><g:link controller="asset" action="index" plugin="httcRepository" data-toggle="tooltip" data-placement="right" data-container="body" title="${message(code:'kola.assets.tooltip')}"><i class="fa fa-fw fa-book"></i> <span class="hide-sidemenu-collapsed"><g:message code="kola.assets" /></span></g:link></li>
				<li class="${controllerName == 'question' ? 'active' : ''}"><g:link controller="question" action="index" plugin="httcQaa" data-toggle="tooltip" data-placement="right" data-container="body" title="${message(code:'kola.questions.tooltip')}"><i class="httc-fw httc-comments"></i> <span class="hide-sidemenu-collapsed"><g:message code="de.httc.plugin.qaa.questions" /></span></g:link></li>
				<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REFLECTION_QUESTION_CREATOR">
					<li class="${controllerName == 'reflectionQuestion' ? 'active' : ''}"><g:link controller="reflectionQuestion" action="index" data-toggle="tooltip" data-placement="right" data-container="body" title="${message(code:'kola.reflectionQuestions.tooltip')}"><i class="fa fa-fw fa-lightbulb-o"></i> <span class="hide-sidemenu-collapsed"><g:message code="kola.reflectionQuestions" /></span></g:link></li>
				</sec:ifAnyGranted>
			</ul>
			<sec:ifAllGranted roles="ROLE_ADMIN">
			<ul class="sidemenu">
				<ul class="sidemenu">
					<li class="sidemenu-divider"><span class="hide-sidemenu-collapsed text-info"><g:message code="kola.admin" />:</span></li>
					<li class="${controllerName == 'user' ? 'active' : ''}"><g:link controller="user" action="index" plugin="httcUser" title="${message(code:'kola.admin.users')}"><i class="fa fa-users fa-fw"></i> <span class="hide-sidemenu-collapsed"><g:message code="kola.admin.users" /></span></g:link></li>
					<li class="${controllerName == 'settings' ? 'active' : ''}"><g:link controller="settings" title="${message(code:'de.httc.plugin.common.settings')}"><i class="fa fa-cogs fa-fw"></i> <span class="hide-sidemenu-collapsed"><g:message code="de.httc.plugin.common.settings" /></span></g:link></li>
					<li class="${controllerName == 'taxonomies' ? 'active' : ''}"><g:link controller="taxonomies" action="index" namespace="admin" plugin="httcTaxonomy" title="${message(code:'de.httc.plugin.taxonomy.taxonomies')}"><i class="fa fa-tags fa-fw"></i> <span class="hide-sidemenu-collapsed"><g:message code="de.httc.plugin.taxonomy.taxonomies" /></span></g:link></li>
					<li class="${controllerName == 'backup' ? 'active' : ''}"><g:link controller="backup" title="Backup"><i class="fa fa-cloud fa-fw"></i> <span class="hide-sidemenu-collapsed">Backup</span></g:link></li>
					<li class="${controllerName == 'platformInfo' ? 'active' : ''}"><g:link controller="platformInfo" title="${message(code:'kola.admin.system')}"><i class="fa fa-cubes fa-fw"></i> <span class="hide-sidemenu-collapsed"><g:message code="kola.admin.system" /></span></g:link></li>
				</ul>
			</sec:ifAllGranted>
			<div class="version hide-sidemenu-collapsed">Version <g:meta name="app.version"/></div>
		</div>
	</sec:ifLoggedIn>
	<div id="content-wrapper">
		<div id="site-header" class="container-fluid">
			<sec:ifLoggedIn><a href="#" class="external-toggle-sidemenu site-header-link site-header-control" onclick="$('#sidebar-wrapper').toggleClass('expanded'); return false;"><i class="fa fa-bars"></i></a></sec:ifLoggedIn>
			<a class="site-header-brand" href="${createLink(uri:'/')}"><asset:image src="kola-logo.png" class="logo" alt="KOLA"/> KOLA</a>
			<div class="site-header-right">
				<sec:ifNotLoggedIn>
					<g:link controller="login" class="site-header-link"><i class="fa fa-sign-in fa-lg"></i> <g:message code="kola.signin" /></g:link>
				</sec:ifNotLoggedIn>
				<sec:ifLoggedIn>
					<div class="dropdown">
						<a href="#" class="site-header-link site-header-control dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><i class="fa fa-user fa-lg"></i> <httc:displayName /> <span class="caret"></span></a>
						<ul class="dropdown-menu dropdown-menu-right">
							<li>
								<g:link controller="profile">
									<i class="fa fa-user fa-fw"></i> <g:message code="kola.profile" />
								</g:link>
							</li>
							<li role="separator" class="divider"></li>
							<li><g:link controller="logout"><i class="fa fa-sign-out fa-fw"></i> <g:message code="kola.signout" /></g:link></li>
						</ul>
					</div>
				</sec:ifLoggedIn>
			</div>
		</div><!-- /.container-fluid -->
		<div class="container-fluid" id="main-container">
			<g:layoutBody/>
		</div>
		<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
		<footer class="footer">
			<nav class="navbar navbar-default">
				<div class="container-fluid">
					<div class="navbar-header">
						<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#footer-links" aria-expanded="false">
							<span class="sr-only">Toggle navigation</span>
							<i class="fa toggle-icon"></i>
						</button>
					</div>
					<div class="collapse navbar-collapse" id="footer-links">
						<ul class="nav navbar-nav navbar-right">
							<g:if test="${grailsApplication.config.kola.termsOfUseExisting}"><li><g:link controller="termsOfUse" action="index"><g:message code="kola.termsOfUse" /></g:link></li></g:if>
							<li><a href="http://www.kola-projekt.de/ueber_kola.html" target="_blank"><g:message code="kola.about" /></a></li>
							<li><a href="http://www.kola-projekt.de/ueber_das_projekt.html" target="_blank"><g:message code="kola.projectLink" /></a></li>
							<li><a href="${grailsApplication.config.kola.appDownloadUrl}" id="google-play-link" target="_blank"><asset:image src="google-play-badge.png" /></a></li>
						</ul>
					</div><!-- /.navbar-collapse -->
				</div><!-- /.container-fluid -->
			</nav>
		</footer>
	</div>
</body>
</html>
