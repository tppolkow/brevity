import React from 'react';
import { Redirect } from 'react-router-dom';
import { post } from 'axios';
import { Row, Col } from 'react-bootstrap';
import Dropzone from 'react-dropzone';
import './DocumentUploadForm.css';



class DocumentUploadForm extends React.Component {
  constructor(props) {
    super(props);
    this.props = props;
    this.state = {
      goToChapterSelect: false,
      goToSummary: false,
      data: {}
    };

    this.handleDrop = this.handleDrop.bind(this);
    this.fileInput = React.createRef();
  }

  handleDrop(files) {
    const formData = new FormData();
    formData.append("file", files[0]);

    post(this.props.endpoint, formData)
      .then(res => {
        if (res.data.pdfText !== '') {
          this.setState({ goToSummary: true });
        } else {
          this.setState({ goToChapterSelect: true, data: res.data });
        }
      });
  }

  render() {
    return (
      <div className="page">
        <Row>
          <Col lg={{span: 8, offset: 2}}>
            <h1>Summarizer</h1>
            <p className="blurb">
              Brevity is a tool for generating summaries from textbook PDFs.
              <br/>
              Start by uploading a PDF document below!
            </p>
            <h3>Upload a PDF</h3>
            <Dropzone onDrop={this.handleDrop}>
              {({getRootProps, getInputProps}) => (
                <section className="dnd-upload-container">
                  <div {...getRootProps()}>
                    <input {...getInputProps()} />
                    <p>Drag 'n' drop some files here, or click to select files</p>
                  </div>
                </section>
              )}
            </Dropzone>
          </Col>
        </Row>
        {this.state.goToChapterSelect && <Redirect to={{ pathname: "/chapter-select", state: { data: this.state.data } }} />}
        {this.state.goToSummary && <Redirect to={{ pathname: "/summary", state: { data: this.state.data } }} />}
      </div>
    );
  }
}

export default DocumentUploadForm;