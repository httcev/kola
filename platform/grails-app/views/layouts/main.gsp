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
<body>
	<nav class="navbar navbar-default">
		<div class="container-fluid">
			<!-- Brand and toggle get grouped for better mobile display -->
			<div class="navbar-header">
				<a class="navbar-brand" href="${createLink(uri:'/')}">
					<asset:image src="kola-logo.png" id="logo" alt="KOLA"/>
				</a>
				<button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-1" aria-expanded="false">
					<span class="sr-only">Toggle navigation</span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
					<span class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="${createLink(uri:'/')}">KOLA</a>
			</div>

			<!-- Collect the nav links, forms, and other content for toggling -->
			<div class="collapse navbar-collapse" id="bs-example-navbar-collapse-1">
				<ul class="nav navbar-nav">
					<li class="active"><a href="#">Link <span class="sr-only">(current)</span></a></li>
					<li><a href="#">Link</a></li>
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Dropdown <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="#">Action</a></li>
							<li><a href="#">Another action</a></li>
							<li><a href="#">Something else here</a></li>
							<li role="separator" class="divider"></li>
							<li><a href="#">Separated link</a></li>
							<li role="separator" class="divider"></li>
							<li><a href="#">One more separated link</a></li>
						</ul>
					</li>
				</ul>
				<form class="navbar-form navbar-left" role="search">
					<div class="form-group">
						<input type="search" class="form-control" placeholder="Search">
					</div>
					<button type="submit" class="btn btn-default">Submit</button>
				</form>
				<ul class="nav navbar-nav navbar-right">
				<sec:ifAllGranted roles="ROLE_ADMIN">
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Administration <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><g:link controller="user">Benutzerverwaltung</g:link></li>
						</ul>
					</li>
				</sec:ifAllGranted>
				<sec:ifNotLoggedIn>
					<li><g:link controller="login">Anmelden</g:link></li>
				</sec:ifNotLoggedIn>
				<sec:ifLoggedIn>
					<li class="dropdown">
						<a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><kola:displayName /> <span class="caret"></span></a>
						<ul class="dropdown-menu">
							<li><a href="#">Action</a></li>
							<li><a href="#">Another action</a></li>
							<li><g:link controller="profile">Profil</g:link></li>
							<li role="separator" class="divider"></li>
							<li><g:link controller="logout">Abmelden</g:link></li>
						</ul>
					</li>
				</sec:ifLoggedIn>
				</ul>
			</div><!-- /.navbar-collapse -->
		</div><!-- /.container-fluid -->
	</nav>
	<div class="container-fluid">
		<g:layoutBody/>
	</div>
	<div id="spinner" class="spinner" style="display:none;"><g:message code="spinner.alt" default="Loading&hellip;"/></div>
</body>
</html>
