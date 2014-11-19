/**
 * @jsx React.DOM
 */

var CommentForm = React.createClass({
  handleSubmit: function(e) {
    e.preventDefault();
    var message = this.refs.text.getDOMNode().value.trim();
    if (!text || !author) {
      return;
    }
    this.props.onCommentSubmit({author: author, message: message});
    this.refs.author.getDOMNode().value = '';
    this.refs.text.getDOMNode().value = '';
    return;
  },
  render: function() {
    return (
        <form className="commentForm" action="/say" method="GET">
        <input type="text" name="chat_message" placeholder="Think carefully" ref="text" />
        <input type="submit" value="Ask" />
        </form>
    );
  }
});

var converter = new Showdown.converter();

var ScoreForm = React.createClass({
  handleUpvote: function(e) {
    e.preventDefault();
    return $.post( this.props.up_vote_url, function( data ) {    });
  },
  handleDownvote: function(e) {
    e.preventDefault();
    return $.post( this.props.down_vote_url, function( data ) {    });
  },
  render: function() {
    return (
        <div>
        <form className="scoreForm" onSubmit={this.handleUpvote}>
        <button className="up btn btn-success glyphicon glyphicon-arrow-up " ></button>
        </form>
        <form className="scoreForm" onSubmit={this.handleDownvote}>
        <button className="down btn btn-danger  glyphicon glyphicon-arrow-down  " ></button>
        </form>
        </div>
    );
  }
});

var Comment = React.createClass({
  render: function() {
    var rawMarkup = converter.makeHtml(this.props.children.toString());
    var message_id = this.props.id;
    var user_id = document.cookie.split("=")[2] || document.cookie.split("=")[1];
		console.log (message_id);
		console.log (user_id);
    var down_url = "/downvote/" + room_id + "/" + message_id + "/" + user_id;
    var up_url =   "/upvote/"  + room_id + "/" + message_id + "/" + user_id;
    return (
        <div className="question panel">
        <h2 className="col-xs-2">
        {this.props.score}
      </h2>
        <div className="col-xs-8">
        <span dangerouslySetInnerHTML={{__html: rawMarkup}} />
        </div>
        <div className="button col-xs-2">
        <ScoreForm onScoreSubmit={this.handleCommentSubmit} up_vote_url={up_url} down_vote_url={down_url} />
        </div>
        </div>
    );
  }
});

var CommentList = React.createClass({
  render: function() {
    var commentNodes = this.props.data.map(function(comment, index) {
      return (
        // `key` is a React-specific concept and is not mandatory for the
        // purpose of this tutorial. if you're curious, see more here:
        // http://facebook.github.io/react/docs/multiple-components.html#dynamic-children
          <Comment key={index} score={comment.score} id={comment.id}>
          {comment.message}
        </Comment>
      );
    });
    return (
        <div className="commentList">
        {commentNodes}
      </div>

    );
  }
});

var CommentBox = React.createClass({
  loadCommentsFromServer: function() {
    $.ajax({
      url: this.props.url,
      dataType: 'json',
      success: function(data) {
        this.setState({data: data});
      }.bind(this),
      error: function(xhr, status, err) {
        console.error(this.props.url, status, err.toString());
      }.bind(this)
    });
  },
  handleCommentSubmit: function(comment) {
    var comments = this.state.data;
    comments.push(comment);
    this.setState({data: comments}, function() {
      // `setState` accepts a callback. To avoid (improbable) race condition,
      // `we'll send the ajax request right after we optimistically set the new
      // `state.
      $.ajax({
        url: this.props.url,
        dataType: 'json',
        type: 'POST',
        data: comment,
        success: function(data) {
          this.setState({data: data});
        }.bind(this),
        error: function(xhr, status, err) {
          console.error(this.props.url, status, err.toString());
        }.bind(this)
      });
    });
  },
  getInitialState: function() {
    return {data: []};
  },
  componentDidMount: function() {
    this.loadCommentsFromServer();
    setInterval(this.loadCommentsFromServer, this.props.pollInterval);
  },
  render: function() {
    return (
        <div className="commentBox">
        <h1>{this.props.room}</h1>
        <CommentList data={this.state.data} />
        <br/>
        <br/>
        <CommentForm onCommentSubmit={this.handleCommentSubmit} />
        </div>
    );
  }
});

room_id = document.URL.split("/").pop();
json_url = "json/" + room_id;

React.renderComponent(
    <CommentBox url={json_url} room={room_id} pollInterval={2000} />,
  document.getElementById('content')
);
