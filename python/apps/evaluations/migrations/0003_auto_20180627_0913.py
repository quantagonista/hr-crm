# Generated by Django 2.0.6 on 2018-06-27 09:13

from django.conf import settings
from django.db import migrations, models
import django.db.models.deletion


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('evaluations', '0002_evaluation_interview'),
        ('departments', '0001_initial'),
        migrations.swappable_dependency(settings.AUTH_USER_MODEL),
    ]

    operations = [
        migrations.AddField(
            model_name='evaluation',
            name='reviewer',
            field=models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, to=settings.AUTH_USER_MODEL),
        ),
        migrations.AddField(
            model_name='criteria',
            name='department',
            field=models.ForeignKey(on_delete=django.db.models.deletion.PROTECT, to='departments.Department'),
        ),
    ]
