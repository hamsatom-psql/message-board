import * as React from "react";

const headStyle = {
    gridArea: "head",
    alignSelf: "start",
    justifySelf: "start",
    maxWidth: "100%",
    minWidth: "0",
    fontFamily: "monospace",
}

const MessageHead = ({author, createdAt, modifiedTimestamp}) => {
    return <header style={headStyle}>
        {author}&nbsp;Created: <time>{new Date(createdAt).toLocaleString()}</time>&nbsp;
        {modifiedTimestamp !== createdAt && <>Modified: <time>{new Date(modifiedTimestamp).toLocaleString()}</time>&nbsp;</>}
    </header>;
}

export default MessageHead