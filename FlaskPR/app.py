from flask import Flask,render_template,request

app =Flask(__name__)

@app.route("/")
<<<<<<< HEAD
def indes():
    return render_template("index.html",name="mera")
app.run(debug=True)
=======
def index():
    return render_template("index.html")


if __name__=="__main__":
    app.run(debug=True)
>>>>>>> b288eee462fb07b2d2376fd8c55a1dd044b1294e
