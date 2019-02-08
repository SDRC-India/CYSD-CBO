
	<div id="spinner" class="loader" style="display: none;"></div>
	<div id="loader-mask" class="loader" style="display: none;"></div>

	<div class="container modern-p-form">
		<div class="login-container">
			<div id="login" class="p-shadowed">

				<div class="form-container">
					<h3>Login</h3>
					<form action="login" method="post"
						class="modern-p-form p-form-modern-purple">
						<div class="">
							<div class="form-group">
								<label for="email1">User Name</label>
								<div class="input-group p-has-icon">
									<input type="text" id="username" name="username"
										placeholder="username" class="form-control" required
										oninvalid="this.setCustomValidity('Please input your username')"
										oninput="setCustomValidity('')">
								</div>
							</div>
						</div>
						<div class="">
							<div class="form-group">
								<label for="password">Password</label>
								<div class="input-group p-has-icon">
									<input type="password" id="password" name="password"
										placeholder="password" class="form-control" required
										oninvalid="this.setCustomValidity('Please input your password')"
										oninput="setCustomValidity('')">
									</div>
							</div>
						</div>
						<div class="form-group">
							<div class="input-group text-center">
								<button class="btn loginbtn" type="submit" style="width: 150px;">Submit</button>
							</div>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>

