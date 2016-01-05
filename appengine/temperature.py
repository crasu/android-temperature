import webapp2
import json
from google.appengine.ext import db
import time
import logging

class MainPage(webapp2.RequestHandler):
    def get(self):
        self.response.headers.add_header("Access-Control-Allow-Origin", "*")
        self.response.headers['Content-Type'] = 'text/plain'
        self.response.write(json.dumps(getTemperatures()))

    def post(self):
        self.response.headers['Content-Type'] = 'text/plain'
        temperature = self.request.get("temperature")
        id = self.request.get("id")
        logging.info("Received temperature from client: " + temperature)
        save(temperature, id)


def getTemperatures():
    l = []
    temperatures = db.GqlQuery("SELECT * FROM Temperature ORDER BY time DESC")
    for temperature in temperatures[0:100]:
            d = {"time": temperature.time, "temperature": temperature.temperature}
            l.append(d)
    return l

def save(temperature, id):
    t = Temperature()
    t.temperature = int(temperature)
    t.time = int(time.time())
    t.id = id
    t.put()


class Temperature(db.Model):
    temperature = db.IntegerProperty()
    time = db.IntegerProperty()
    id = db.StringProperty()


application = webapp2.WSGIApplication([('/temperature', MainPage)], debug=True)

