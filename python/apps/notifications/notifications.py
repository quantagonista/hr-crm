from fcm_django.models import FCMDevice


def candidate_created(sender, **kwargs):
    if kwargs['created']:
        device = FCMDevice.objects.all()
        candidate = kwargs['instance']
        if candidate:
            message = {
                'title': 'New Candidate',
                'body': candidate.email
            }
            device.send_message(**message)


def interview_created(sender, **kwargs):
    if kwargs['created']:
        device = FCMDevice.objects.all()
        interview = kwargs['instance']
        candidate = interview.candidate
        message = {
            'title': 'Interview with: ' + str(candidate.email),
            'body': 'begin - ' + interview.begin_time.strftime("%A, %d. %B %Y %I:%M%p")
        }
        device.send_message(**message)


def request_created(sender, **kwargs):
    if kwargs['created']:
        device = FCMDevice.objects.all()
        request_name = kwargs['instance'].position.name
        message = {
            'title': 'New Request!',
            'body': request_name
        }
        device.send_message(**message)
