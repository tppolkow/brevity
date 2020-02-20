import React from 'react';
import { Redirect } from 'react-router-dom';
import { post } from 'axios';
import { Button, Form } from 'react-bootstrap';
import Config from './Config';
import './ChapterSelectorList.css';

class ChapterSelectorList extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.state = {
      goToSummary: false
    };

    this.handleInputChange = this.handleInputChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
  }

  handleInputChange(e) {
    const target = e.target;
    this.setState({ [target.id]: target.checked });
  }

  handleSubmit(e) {
    e.preventDefault();

    const selectedChapters = Object.entries(this.state)
      .reduce((selected, [chapId, checked]) => {
          if (checked) {
            const id = chapId.split('-')[1];
            selected.push(this.props.location.state.data.chapters[id]);
          }
          return selected;
      }, []);

    const reqBody = {
      ...this.props.location.state.data,
      chapters: selectedChapters
    };

    post(Config.serverUrl + "/upload/chapters", reqBody)
      .then(res => this.setState({ goToSummary: true, }));
  }

  chapterItems(chapters) {
    if (chapters === undefined) return [];

    return chapters.map((chapter, index) =>
      <div className="chapter-selector-item" key={index}>
        <Form.Check
          name={chapter.title}
          id={`chapter-${index}`}
          type="checkbox"
          label={chapter.title}
          checked={this.state.chapter}
          onChange={this.handleInputChange}
        />
      </div>
    );
  }

  render() {
    const { chapters } = this.props.location.state.data ? this.props.location.state.data : [];

    return (
      <div>
        <Form onSubmit={this.handleSubmit}>
          <h4><Form.Label>Select chapters to summarize</Form.Label></h4>
          {this.chapterItems(chapters)}
          <Button variant="primary" type="submit">Select</Button>
        </Form>
        {this.state.goToSummary && <Redirect to={{ pathname: "/summary", state: this.state.data }} />}
      </div>
    );
  }

}

export default ChapterSelectorList;