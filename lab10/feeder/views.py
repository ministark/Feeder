from django.shortcuts import render, HttpResponse, HttpResponseRedirect
from .models import Instructor, Course, Student
from django.urls import reverse
from django.views import generic
from .forms import InstructorForm, LoginForm
from django.contrib.auth.models import User
from django.contrib.auth import authenticate, login, logout
from django.contrib.auth.decorators import login_required
# Create your views here.

# class IndexView(generic.FormView):
# 	template_name = "feeder/index.html"
# 	form_class = LoginForm
# 	success_url = '/feeder/index'
from django.contrib.auth import views

def change_password(request):
    template_response = views.password_change(request)
    # Do something with `template_response`
    return template_response

def LoginView(request):
	if request.method == "POST":
		form = LoginForm(request.POST)
		# if form.is_valid():
		user = authenticate(username=request.POST['email'], password=request.POST['password'])
		if user is not None:
			if hasattr(user, 'instructor'):
				login(request, user)
				welcome = "Welcome, " + user.get_full_name()
				return render(request, "feeder/index.html", {
					'error_message' : welcome
				})
			else :
				return HttpResponseRedirect(reverse('feeder:login',{'error_message' : "Students cannot login here"}))
		else :
			form = LoginForm(request.POST)
			return render(request, "feeder/login.html", {
				'form' : form,
				'error_message' : "Wrong email or password"
			})
				
	else :
		if request.user.is_authenticated :
			return HttpResponseRedirect(reverse('feeder:index'))
		else :
			form = LoginForm()
	return render(request, "feeder/login.html", {'form' : form } )

@login_required
def IndexView(request):
	return render(request, "feeder/index.html", {'user' : request.user })

@login_required
def Logout(request):
	logout(request)
	return HttpResponseRedirect(reverse('feeder:login'))

def RegisterView(request):
	if request.method == "POST":
		form = InstructorForm(request.POST)
		if form.is_valid():
			try: 
				User.objects.get(username=request.POST['email'])
			except (KeyError, User.DoesNotExist):
				user = User()
				instructor = Instructor()
				user.first_name=request.POST['first_name']
				user.last_name=request.POST['last_name']
				user.username=request.POST['email']
				user.set_password(request.POST['password'])
				user.save()
				instructor.user_id = user.id;
				instructor.save()
				return HttpResponse("You have been successfully registered")
			else :
				error_message="This email has already been taken"
	else :
		form = InstructorForm()
		error_message=''
	return render(request, 'feeder/login.html',{
			'form' : form,
			'error_message' : error_message,
		})