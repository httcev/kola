<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="de" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="de" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="de" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="de" class="no-js ie9"> <![endif]-->
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
	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<a class="navbar-brand" href="${createLink(uri:'/')}">
					<asset:image src="kola-logo.png" id="logo" alt="KOLA"/>
				</a>
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
					<span class="sr-only">Toggle navigation</span>
					<i class="fa fa-fw fa-ellipsis-v"></i>
				</button>
				<a class="navbar-brand" href="${createLink(uri:'/')}">KOLA</a>
			</div>

			<!-- Collect the nav links, forms, and other content for toggling -->
			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
				<sec:ifLoggedIn>
				<ul class="nav navbar-nav">
					<li class="${(controllerName == 'task' && !params.isTemplate?.toBoolean()) ? 'active' : ''}"><g:link controller="task" action="index"><g:message code="kola.tasks" /></g:link></li>
					<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_TASK_TEMPLATE_CREATOR">
						<li class="${(controllerName == 'task' && params.isTemplate?.toBoolean()) ? 'active' : ''}"><g:link controller="task" action="index" params="[isTemplate:true]"><g:message code="kola.taskTemplates" /></g:link></li>
					</sec:ifAnyGranted>
					<li class="${controllerName == 'question' ? 'active' : ''}"><g:link controller="question" action="index"><g:message code="de.httc.plugin.qaa.questions" /></g:link></li>
					<sec:ifAnyGranted roles="ROLE_ADMIN,ROLE_REFLECTION_QUESTION_CREATOR">
						<li class="${controllerName == 'reflectionQuestion' ? 'active' : ''}"><g:link controller="reflectionQuestion" action="index"><g:message code="kola.reflectionQuestions" /></g:link></li>
					</sec:ifAnyGranted>
					<li class="${controllerName == 'asset' ? 'active' : ''}"><g:link controller="asset" action="index" plugin="repository"><g:message code="kola.assets" /></g:link></li>
				</ul>
				</sec:ifLoggedIn>
				<ul class="nav navbar-nav navbar-right">
				<sec:ifAllGranted roles="ROLE_ADMIN">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><i class="fa fa-wrench fa-lg"></i> <g:message code="kola.admin" /> <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><g:link controller="user" action="index" plugin="user"><i class="fa fa-users fa-fw"></i> <g:message code="kola.admin.users" /></g:link></li>
							<li><g:link controller="settings"><i class="fa fa-cogs fa-fw"></i> <g:message code="kola.settings" /></g:link></li>
							<li><g:link controller="backup"><i class="fa fa-cloud fa-fw"></i> Backup</g:link></li>
							<li><g:link controller="platformInfo"><i class="fa fa-cubes fa-fw"></i> <g:message code="kola.admin.system" /></g:link></li>
						</ul>
					</li>
				</sec:ifAllGranted>
				<sec:ifNotLoggedIn>
					<li><g:link controller="login"><i class="fa fa-sign-in fa-lg"></i> <g:message code="kola.signin" /></g:link></li>
				</sec:ifNotLoggedIn>
				<sec:ifLoggedIn>
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><i class="fa fa-user fa-lg"></i> <kola:displayName /> <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li>
								<g:link controller="profile">
									<i class="fa fa-user fa-fw"></i> <g:message code="kola.profile" />
								</g:link>
							</li>
							<li role="separator" class="divider"></li>
							<li><g:link controller="logout"><i class="fa fa-sign-out fa-fw"></i> <g:message code="kola.signout" /></g:link></li>
						</ul>
					</li>
				</sec:ifLoggedIn>
				</ul>
			</div><!-- /.navbar-collapse -->
		</div><!-- /.container-fluid -->
	</nav>
	<div class="container-fluid" id="main-container">
		<g:layoutBody/>
	</div>
	<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
	<footer class="footer">
		<nav class="navbar navbar-default">
			<div class="container-fluid">
			    <!-- Brand and toggle get grouped for better mobile display -->
			    <div class="navbar-header">
			      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#footer-links" aria-expanded="false">
			        <span class="sr-only">Toggle navigation</span>
			        <span class="toggle-icon"></span>
			      </button>
			      <div class="navbar-brand version">Version <g:meta name="app.version"/></div>
			    </div>
			    <!-- Collect the nav links, forms, and other content for toggling -->
			    <div class="collapse navbar-collapse" id="footer-links">
					<ul class="nav navbar-nav navbar-right">
						<g:if test="${grailsApplication.config.kola.termsOfUseExisting}"><li><g:link controller="termsOfUse" action="index"><g:message code="kola.termsOfUse" /></g:link></li></g:if>
						<li><a href="http://www.kola-projekt.de/ueber_kola.html" target="_blank"><g:message code="kola.about" /></a></li>
						<li><a href="http://www.kola-projekt.de/ueber_das_projekt.html" target="_blank"><g:message code="kola.projectLink" /></a></li>
						<sec:ifLoggedIn><li><a href="${grailsApplication.config.kola.appDownloadUrl}" id="google-play-link" target="_blank"><asset:image src="google-play-badge.png" /></a></li></sec:ifLoggedIn>
					</ul>
				</div><!-- /.navbar-collapse -->
			</div><!-- /.container-fluid -->
		</nav>
	</footer>
</body>
</html>
