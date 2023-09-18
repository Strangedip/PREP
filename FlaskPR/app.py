from flask import Flask,render_template,request

app =Flask(__name__)

@app.route("/")
def indes():
    return render_template("index.html",name="mera")
app.run(debug=True)