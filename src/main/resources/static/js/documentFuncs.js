var stompClient = null;
var contentsCopy = Array.from(document.getElementById('editor').children).map(formatArray);
const editor = document.getElementById('editor');
var dos = '';

function formatArray(item) {
    let arr = [item.innerHTML];
    if (item.attributes.align != null)
        arr.push(item.attributes.align.value);
    else
        arr.push(null);
    return arr;
}

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

editor.addEventListener('input', e => {
    dos = Array.from(e.target.children);
    document.getElementById('saving').style.visibility = null;
    delay(function () {
        document.getElementById('saving').style.visibility = "hidden";
        if (dos.length === 0 && !e.target.innerHTML.startsWith("<div>")) {
            let innerDiv = document.createElement("div");
            innerDiv.innerHTML = e.target.innerHTML;
            editor.innerText = "";
            editor.appendChild(innerDiv);
            dos = [innerDiv];
        }
        let toSend = Array();
        let limit = contentsCopy.length;
        let align = null;
        if (contentsCopy.length < dos.length) {
            for (let i = contentsCopy.length; i < dos.length; i++) {
                align = null;
                if (dos[i].attributes.align != null)
                    align = dos[i].attributes.align.value;
                toSend.push({number: i, data: dos[i].innerHTML, align: align})
            }
            stompClient.send("/app/send", {}, JSON.stringify({
                'documentId': parseInt(document.getElementById("document_id").innerText),
                'fromUser': document.getElementById("userName").innerText,
                'command': 'create',
                'content': toSend
            }));
        }
        if (contentsCopy.length > dos.length) {
            for (let i = dos.length; i < contentsCopy.length; i++) {
                toSend.push({number: i, data: contentsCopy[i][0], align: null})
            }
            stompClient.send("/app/send", {}, JSON.stringify({
                'documentId': parseInt(document.getElementById("document_id").innerText),
                'fromUser': document.getElementById("userName").innerText,
                'command': 'delete',
                'content': toSend
            }));
            limit = dos.length;
        }

        for (let i = 0; i < limit; i++) {
            if (contentsCopy[i][0] !== dos[i].innerHTML ||
                (dos[i].attributes.align != null && contentsCopy[i][1] !== dos[i].attributes.align.value) ||
                (dos[i].attributes.align == null && contentsCopy[i][1] != dos[i].attributes.align)) {
                align = null;
                if (dos[i].attributes.align != null)
                    align = dos[i].attributes.align.value;
                toSend.push({number: i, data: dos[i].innerHTML, align: align})
            }
        }
        stompClient.send("/app/send", {}, JSON.stringify({
            'documentId': parseInt(document.getElementById("document_id").innerText),
            'fromUser': document.getElementById("userName").innerText,
            'command': 'edit',
            'content': toSend
        }));
        document.getElementById("lastEdited").innerText = `Последнее изменение от ${document.getElementById("userName").innerText}`;
        contentsCopy = Array.from(dos).map(formatArray);
    }, 1000);
});

var delay = (function () {
    let timer = 0;
    return function (callback, ms) {
        clearTimeout(timer);
        timer = setTimeout(callback, ms);
    };
})();

function connect() {
    var socket = new SockJS('/message-ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/messages', function (message) {
            let response = JSON.parse(message.body)
            if (response.documentId === parseInt(document.getElementById("document_id").innerText) &&
                response.fromUser !== document.getElementById("userName").innerText) {
                if (response.command === 'create') {
                    for (let contentElement of response.content) {
                        let innerDiv = document.createElement("div");
                        innerDiv.innerHTML = contentElement.data;
                        innerDiv.setAttribute("align", contentElement.align);
                        editor.appendChild(innerDiv);
                    }
                }
                if (response.command === 'delete') {
                    for (let i = 0; i < response.content.length; i++) {
                        editor.removeChild(editor.children[response.content[0].number]);
                    }
                }
                if (response.command === 'edit') {
                    for (let contentElement of response.content) {
                        let innerDiv = document.createElement("div");
                        innerDiv.innerHTML = contentElement.data;
                        innerDiv.setAttribute("align", contentElement.align);
                        editor.children[contentElement.number].replaceWith(innerDiv);
                    }
                }
                document.getElementById("lastEdited").innerText = `Последнее изменение от ${response.fromUser}`;
                contentsCopy = Array.from(document.getElementById('editor').children).map(formatArray);
            }
        });
    });
}

connect();