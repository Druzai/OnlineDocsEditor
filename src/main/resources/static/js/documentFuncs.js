function changeRightLevel() {
    const level = document.getElementById('rightLevel').value;
    if (level == 1) {
        document.getElementById("editor").setAttribute("contenteditable", "true")
    } else {
        document.getElementById("editor").setAttribute("contenteditable", "false")
    }
}

function format(command, value) {
    document.execCommand(command, false, value);
}

function chooseColor() {
    const color = document.getElementById('myColor').value;
    document.execCommand('foreColor', false, color);
}

function changeFont() {
    const font = document.getElementById('input-font').value;
    document.execCommand('fontName', false, font);
}

function changeSize() {
    const size = document.getElementById('fontSize').value;
    document.execCommand('fontSize', false, size);
}